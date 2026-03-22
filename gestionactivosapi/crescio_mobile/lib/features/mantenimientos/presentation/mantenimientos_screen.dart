import 'package:flutter/material.dart';

class MantenimientosScreen extends StatelessWidget {
  const MantenimientosScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      physics: const AlwaysScrollableScrollPhysics(),
      children: [
        Row(
          children: [
            Expanded(
              child: DropdownButtonFormField<String>(
                items: const [
                  DropdownMenuItem(value: 'todos', child: Text('Todos')),
                  DropdownMenuItem(value: 'pendiente', child: Text('Pendiente')),
                  DropdownMenuItem(value: 'cerrado', child: Text('Cerrado')),
                ],
                onChanged: (_) {},
                decoration: const InputDecoration(labelText: 'Estado'),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: TextField(
                decoration: const InputDecoration(
                  labelText: 'Fecha',
                  prefixIcon: Icon(Icons.calendar_today_outlined),
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),
        const Card(
          child: ListTile(
            title: Text('Nuevo mantenimiento'),
            subtitle: Text('Flujo paso a paso pendiente de implementar sobre esta base.'),
          ),
        ),
      ],
    );
  }
}
