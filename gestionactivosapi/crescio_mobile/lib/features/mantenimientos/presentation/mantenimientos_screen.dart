import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../core/network/api_client.dart';

class MantenimientosScreen extends StatefulWidget {
  const MantenimientosScreen({super.key});

  @override
  State<MantenimientosScreen> createState() => _MantenimientosScreenState();
}

class _MantenimientosScreenState extends State<MantenimientosScreen> {
  String _estado = 'todos';
  late Future<List<Map<String, dynamic>>> _future;

  @override
  void initState() {
    super.initState();
    _future = _loadMantenimientos();
  }

  Future<List<Map<String, dynamic>>> _loadMantenimientos() async {
    final apiClient = context.read<ApiClient>();
    final data = await apiClient.get('/mantenimiento');
    final items = (data as List)
        .map((item) => Map<String, dynamic>.from(item as Map))
        .toList();
    if (_estado == 'todos') {
      return items;
    }
    return items
        .where((item) => _text(item['estadoInterno']).toLowerCase() == _estado.toLowerCase())
        .toList();
  }

  Future<void> _reload() async {
    final future = _loadMantenimientos();
    setState(() => _future = future);
    await future;
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        DropdownButtonFormField<String>(
          initialValue: _estado,
          items: const [
            DropdownMenuItem(value: 'todos', child: Text('Todos')),
            DropdownMenuItem(value: 'EN_PROCESO', child: Text('En proceso')),
            DropdownMenuItem(value: 'CERRADO', child: Text('Cerrado')),
            DropdownMenuItem(value: 'PENDIENTE', child: Text('Pendiente')),
          ],
          onChanged: (value) {
            if (value == null) return;
            _estado = value;
            _reload();
          },
          decoration: const InputDecoration(labelText: 'Estado'),
        ),
        const SizedBox(height: 16),
        Expanded(
          child: FutureBuilder<List<Map<String, dynamic>>>(
            future: _future,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const Center(child: CircularProgressIndicator());
              }
              if (snapshot.hasError) {
                return _ErrorState(
                  message: 'No fue posible cargar los mantenimientos.',
                  onRetry: _reload,
                );
              }
              final items = snapshot.data ?? const [];
              if (items.isEmpty) {
                return RefreshIndicator(
                  onRefresh: _reload,
                  child: ListView(
                    physics: const AlwaysScrollableScrollPhysics(),
                    children: const [
                      SizedBox(height: 80),
                      Center(child: Text('No hay mantenimientos para mostrar.')),
                    ],
                  ),
                );
              }

              return RefreshIndicator(
                onRefresh: _reload,
                child: ListView.separated(
                  physics: const AlwaysScrollableScrollPhysics(),
                  itemCount: items.length,
                  separatorBuilder: (_, _) => const SizedBox(height: 12),
                  itemBuilder: (context, index) {
                    final item = items[index];
                    return Card(
                      child: ListTile(
                        leading: const Icon(Icons.build_outlined),
                        title: Text(_text(item['equipoCodigoSap'], fallback: 'Sin codigo')),
                        subtitle: Text(
                          '${_text(item['equipoDescripcion'])}\n'
                          'Tecnico: ${_text(item['tecnicoNombre'])}\n'
                          'Estado: ${_text(item['estadoInterno'])}',
                        ),
                        isThreeLine: true,
                        trailing: Text(_text(item['fechaMantenimiento'], fallback: '')),
                      ),
                    );
                  },
                ),
              );
            },
          ),
        ),
      ],
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
