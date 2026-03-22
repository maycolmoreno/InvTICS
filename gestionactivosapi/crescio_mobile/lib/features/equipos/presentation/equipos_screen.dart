import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../core/network/api_client.dart';

class EquiposScreen extends StatefulWidget {
  const EquiposScreen({super.key});

  @override
  State<EquiposScreen> createState() => _EquiposScreenState();
}

class _EquiposScreenState extends State<EquiposScreen> {
  final _searchController = TextEditingController();
  late Future<List<Map<String, dynamic>>> _future;

  @override
  void initState() {
    super.initState();
    _future = _loadEquipos();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<List<Map<String, dynamic>>> _loadEquipos() async {
    final apiClient = context.read<ApiClient>();
    final data = await apiClient.get('/equipos');
    final items = (data as List)
        .map((item) => Map<String, dynamic>.from(item as Map))
        .toList();
    items.sort((a, b) => _text(a['codigoSap']).compareTo(_text(b['codigoSap'])));
    return items;
  }

  Future<void> _refresh() async {
    final future = _loadEquipos();
    setState(() => _future = future);
    await future;
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        TextField(
          controller: _searchController,
          onChanged: (_) => setState(() {}),
          decoration: const InputDecoration(
            hintText: 'Buscar por serial, modelo o codigo',
            prefixIcon: Icon(Icons.search),
          ),
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
                  message: 'No fue posible cargar los equipos.',
                  onRetry: _refresh,
                );
              }

              final equipos = _filter(snapshot.data ?? const []);
              if (equipos.isEmpty) {
                return RefreshIndicator(
                  onRefresh: _refresh,
                  child: ListView(
                    physics: const AlwaysScrollableScrollPhysics(),
                    children: const [
                      SizedBox(height: 80),
                      Center(child: Text('No hay equipos para mostrar.')),
                    ],
                  ),
                );
              }

              return RefreshIndicator(
                onRefresh: _refresh,
                child: ListView.separated(
                  physics: const AlwaysScrollableScrollPhysics(),
                  itemCount: equipos.length,
                  separatorBuilder: (_, _) => const SizedBox(height: 12),
                  itemBuilder: (context, index) {
                    final equipo = equipos[index];
                    return Card(
                      child: ListTile(
                        leading: const Icon(Icons.computer_outlined),
                        title: Text(_text(equipo['codigoSap'], fallback: 'Sin codigo')),
                        subtitle: Text(
                          [
                            _text(equipo['tipoEquipo']),
                            _text(equipo['modelo']),
                            'Serial: ${_text(equipo['serial'])}',
                          ].where((part) => part.isNotEmpty).join('\n'),
                        ),
                        isThreeLine: true,
                        trailing: Text(_text(equipo['estadoEquipo'], fallback: '')),
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

  List<Map<String, dynamic>> _filter(List<Map<String, dynamic>> items) {
    final query = _searchController.text.trim().toLowerCase();
    if (query.isEmpty) {
      return items;
    }
    return items.where((item) {
      final haystack = [
        _text(item['codigoSap']),
        _text(item['serial']),
        _text(item['modelo']),
        _text(item['tipoEquipo']),
      ].join(' ').toLowerCase();
      return haystack.contains(query);
    }).toList();
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
