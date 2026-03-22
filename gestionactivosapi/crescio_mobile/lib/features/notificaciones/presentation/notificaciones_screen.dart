import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../core/network/api_client.dart';

class NotificacionesScreen extends StatefulWidget {
  const NotificacionesScreen({super.key});

  @override
  State<NotificacionesScreen> createState() => _NotificacionesScreenState();
}

class _NotificacionesScreenState extends State<NotificacionesScreen> {
  late Future<List<Map<String, dynamic>>> _future;

  @override
  void initState() {
    super.initState();
    _future = _loadNotificaciones();
  }

  Future<List<Map<String, dynamic>>> _loadNotificaciones() async {
    final apiClient = context.read<ApiClient>();
    final data = await apiClient.get('/notificaciones');
    return (data as List)
        .map((item) => Map<String, dynamic>.from(item as Map))
        .toList();
  }

  Future<void> _reload() async {
    final future = _loadNotificaciones();
    setState(() => _future = future);
    await future;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Notificaciones')),
      body: FutureBuilder<List<Map<String, dynamic>>>(
        future: _future,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }
          if (snapshot.hasError) {
            return _ErrorState(
              message: 'No fue posible cargar las notificaciones.',
              onRetry: _reload,
            );
          }
          final items = snapshot.data ?? const [];
          if (items.isEmpty) {
            return RefreshIndicator(
              onRefresh: _reload,
              child: ListView(
                padding: const EdgeInsets.all(16),
                physics: const AlwaysScrollableScrollPhysics(),
                children: const [
                  SizedBox(height: 80),
                  Center(child: Text('Sin notificaciones nuevas')),
                ],
              ),
            );
          }

          return RefreshIndicator(
            onRefresh: _reload,
            child: ListView.separated(
              padding: const EdgeInsets.all(16),
              physics: const AlwaysScrollableScrollPhysics(),
              itemCount: items.length,
              separatorBuilder: (_, _) => const SizedBox(height: 12),
              itemBuilder: (context, index) {
                final item = items[index];
                final leida = item['leida'] == true;
                return Card(
                  child: ListTile(
                    leading: Icon(
                      leida ? Icons.drafts_outlined : Icons.notifications_active_outlined,
                    ),
                    title: Text(_text(item['mensaje'], fallback: 'Sin mensaje')),
                    subtitle: Text(
                      'Mantenimiento: ${_text(item['referenciaMantenimientoId'])}\n'
                      '${_text(item['creadoEn'], fallback: '')}',
                    ),
                    isThreeLine: true,
                    trailing: Text(leida ? 'Leida' : 'Nueva'),
                  ),
                );
              },
            ),
          );
        },
      ),
    );
  }
}

class _ErrorState extends StatelessWidget {
  const _ErrorState({
    required this.message,
    required this.onRetry,
  });

  final String message;
  final Future<void> Function() onRetry;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(message),
          const SizedBox(height: 12),
          OutlinedButton(
            onPressed: onRetry,
            child: const Text('Reintentar'),
          ),
        ],
      ),
    );
  }
}

String _text(dynamic value, {String fallback = '-'}) {
  final text = value?.toString().trim() ?? '';
  return text.isEmpty ? fallback : text;
}
