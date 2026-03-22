import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../core/network/api_client.dart';

class TicketsScreen extends StatefulWidget {
  const TicketsScreen({super.key});

  @override
  State<TicketsScreen> createState() => _TicketsScreenState();
}

class _TicketsScreenState extends State<TicketsScreen> {
  String _estado = 'todos';
  late Future<List<Map<String, dynamic>>> _future;

  @override
  void initState() {
    super.initState();
    _future = _loadTickets();
  }

  Future<List<Map<String, dynamic>>> _loadTickets() async {
    final apiClient = context.read<ApiClient>();
    final path = _estado == 'todos' ? '/tickets' : '/tickets?estado=${_estado.toUpperCase()}';
    final data = await apiClient.get(path);
    return (data as List)
        .map((item) => Map<String, dynamic>.from(item as Map))
        .toList();
  }

  Future<void> _reload() async {
    final future = _loadTickets();
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
            DropdownMenuItem(value: 'abierto', child: Text('Abierto')),
            DropdownMenuItem(value: 'asignado', child: Text('Asignado')),
            DropdownMenuItem(value: 'cerrado', child: Text('Cerrado')),
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
                  message: 'No fue posible cargar los tickets.',
                  onRetry: _reload,
                );
              }
              final tickets = snapshot.data ?? const [];
              if (tickets.isEmpty) {
                return RefreshIndicator(
                  onRefresh: _reload,
                  child: ListView(
                    physics: const AlwaysScrollableScrollPhysics(),
                    children: const [
                      SizedBox(height: 80),
                      Center(child: Text('No hay tickets para mostrar.')),
                    ],
                  ),
                );
              }

              return RefreshIndicator(
                onRefresh: _reload,
                child: ListView.separated(
                  physics: const AlwaysScrollableScrollPhysics(),
                  itemCount: tickets.length,
                  separatorBuilder: (_, _) => const SizedBox(height: 12),
                  itemBuilder: (context, index) {
                    final ticket = tickets[index];
                    return Card(
                      child: ListTile(
                        leading: const Icon(Icons.confirmation_num_outlined),
                        title: Text(_text(ticket['titulo'], fallback: 'Sin titulo')),
                        subtitle: Text(
                          'Estado: ${_text(ticket['estado'])}\n'
                          'Equipo: ${_text(ticket['idEquipo'])}\n'
                          '${_text(ticket['descripcion'], fallback: 'Sin descripcion')}',
                        ),
                        isThreeLine: true,
                        trailing: Text(_text(ticket['prioridad'], fallback: '')),
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
