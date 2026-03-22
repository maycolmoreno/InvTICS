import 'package:flutter/material.dart';

class NotificacionesScreen extends StatelessWidget {
  const NotificacionesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Notificaciones')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: const [
          Card(
            child: ListTile(
              leading: Icon(Icons.notifications_none),
              title: Text('Sin notificaciones nuevas'),
              subtitle: Text('El polling local se implementara en la siguiente fase.'),
            ),
          ),
        ],
      ),
    );
  }
}
