import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../core/config/app_config.dart';
import '../../../core/storage/local_database.dart';
import '../../auth/presentation/auth_provider.dart';
import 'server_config_screen.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthProvider>();
    return Scaffold(
      appBar: AppBar(title: const Text('Ajustes')),
      body: FutureBuilder<int>(
        future: LocalDatabase.instance.contarPendientes(),
        builder: (context, snapshot) {
          final pending = snapshot.data ?? 0;
          return ListView(
            padding: const EdgeInsets.all(16),
            children: [
              const Text('Conexion al servidor', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
              const SizedBox(height: 8),
              Card(
                child: ListTile(
                  title: Text('${AppConfig.serverIp}:${AppConfig.serverPort}'),
                  subtitle: const Text('Servidor actual configurado'),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () {
                    Navigator.of(context).push(
                      MaterialPageRoute(builder: (_) => const ServerConfigScreen()),
                    );
                  },
                ),
              ),
              const SizedBox(height: 16),
              const Text('Sesion', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
              Card(
                child: ListTile(
                  title: Text(auth.session?.displayName ?? 'Sin sesion'),
                  subtitle: Text(auth.session?.role ?? ''),
                ),
              ),
              const SizedBox(height: 16),
              const Text('Sincronizacion', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
              Card(
                child: ListTile(
                  title: Text('$pending registro(s) pendientes'),
                  subtitle: const Text('Ultima sincronizacion: pendiente de implementar'),
                ),
              ),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: () => context.read<AuthProvider>().logout(),
                child: const Text('Cerrar sesion'),
              ),
            ],
          );
        },
      ),
    );
  }
}
