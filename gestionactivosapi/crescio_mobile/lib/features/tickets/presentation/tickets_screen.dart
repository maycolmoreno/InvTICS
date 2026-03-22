import 'package:flutter/material.dart';

class TicketsScreen extends StatelessWidget {
  const TicketsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      physics: const AlwaysScrollableScrollPhysics(),
      children: [
        DropdownButtonFormField<String>(
          items: const [
            DropdownMenuItem(value: 'abierto', child: Text('Abierto')),
            DropdownMenuItem(value: 'asignado', child: Text('Asignado')),
            DropdownMenuItem(value: 'revision', child: Text('En revision')),
          ],
          onChanged: (_) {},
          decoration: const InputDecoration(labelText: 'Estado'),
        ),
        const SizedBox(height: 16),
        const Card(
          child: ListTile(
            title: Text('Modulo Tickets'),
            subtitle: Text('Aqui ira el listado de tickets asignados al tecnico.'),
          ),
        ),
      ],
    );
  }
}
