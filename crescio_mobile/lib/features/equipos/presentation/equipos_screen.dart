import 'package:flutter/material.dart';

class EquiposScreen extends StatelessWidget {
  const EquiposScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      physics: const AlwaysScrollableScrollPhysics(),
      children: [
        TextField(
          decoration: const InputDecoration(
            hintText: 'Buscar por serial o modelo',
            prefixIcon: Icon(Icons.search),
          ),
        ),
        const SizedBox(height: 16),
        const Card(
          child: ListTile(
            title: Text('Modulo Equipos'),
            subtitle: Text('Aqui ira el listado online/offline de equipos y su detalle.'),
          ),
        ),
      ],
    );
  }
}
