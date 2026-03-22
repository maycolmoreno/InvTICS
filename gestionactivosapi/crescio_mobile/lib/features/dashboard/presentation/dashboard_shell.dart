import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../shared/widgets/cresio_scaffold.dart';
import '../../../shared/widgets/status_banner.dart';
import '../../auth/presentation/auth_provider.dart';
import '../../configuracion/presentation/settings_screen.dart';
import '../../equipos/presentation/equipos_screen.dart';
import '../../mantenimientos/presentation/mantenimientos_screen.dart';
import '../../notificaciones/presentation/notificaciones_screen.dart';
import '../../tickets/presentation/tickets_screen.dart';
import 'dashboard_provider.dart';

class DashboardShell extends StatefulWidget {
  const DashboardShell({super.key});

  @override
  State<DashboardShell> createState() => _DashboardShellState();
}

class _DashboardShellState extends State<DashboardShell> {
  int _index = 0;

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => DashboardProvider(),
      child: Consumer<DashboardProvider>(
        builder: (context, dashboard, _) {
          final pages = [
            _HomeTab(
              pendingOffline: dashboard.pendingOffline,
              offline: dashboard.offline,
            ),
            const MantenimientosScreen(),
            const TicketsScreen(),
            const EquiposScreen(),
            const _MoreTab(),
          ];

          return CresioScaffold(
            title: _titleForIndex(_index),
            bottomNavigationBar: NavigationBar(
              selectedIndex: _index,
              onDestinationSelected: (value) => setState(() => _index = value),
              destinations: [
                const NavigationDestination(icon: Icon(Icons.home_outlined), label: 'Inicio'),
                const NavigationDestination(icon: Icon(Icons.build_outlined), label: 'Mantenimientos'),
                NavigationDestination(
                  icon: Badge(
                    isLabelVisible: dashboard.pendingOffline > 0,
                    label: Text('${dashboard.pendingOffline}'),
                    child: const Icon(Icons.confirmation_num_outlined),
                  ),
                  label: 'Tickets',
                ),
                const NavigationDestination(icon: Icon(Icons.computer_outlined), label: 'Equipos'),
                const NavigationDestination(icon: Icon(Icons.more_horiz), label: 'Mas'),
              ],
            ),
            floatingActionButton: _index == 0
                ? FloatingActionButton.extended(
                    onPressed: () => setState(() => _index = 1),
                    icon: const Icon(Icons.add),
                    label: const Text('+Mantenimiento'),
                  )
                : null,
            child: RefreshIndicator(
              onRefresh: dashboard.refresh,
              child: pages[_index],
            ),
          );
        },
      ),
    );
  }

  String _titleForIndex(int index) {
    switch (index) {
      case 1:
        return 'Mantenimientos';
      case 2:
        return 'Tickets';
      case 3:
        return 'Equipos';
      case 4:
        return 'Mas opciones';
      default:
        return 'CRESIO Mobile';
    }
  }
}

class _HomeTab extends StatelessWidget {
  const _HomeTab({
    required this.pendingOffline,
    required this.offline,
  });

  final int pendingOffline;
  final bool offline;

  @override
  Widget build(BuildContext context) {
    return ListView(
      physics: const AlwaysScrollableScrollPhysics(),
      children: [
        if (offline)
          const Padding(
            padding: EdgeInsets.only(bottom: 12),
            child: StatusBanner(
              text: 'Sin conexion - modo offline',
              backgroundColor: Color(0xFFFFF3CD),
              textColor: Color(0xFF8A6300),
            ),
          ),
        Wrap(
          spacing: 12,
          runSpacing: 12,
          children: const [
            _SummaryCard(title: 'Visitas hoy', value: '0'),
            _SummaryCard(title: 'Tickets asignados', value: '0'),
            _SummaryCard(title: 'Pend. firma', value: '0'),
          ],
        ),
        const SizedBox(height: 12),
        _SummaryCard(title: 'Pendientes offline', value: '$pendingOffline', fullWidth: true),
        const SizedBox(height: 20),
        const Text(
          'Visitas programadas para hoy',
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.w700),
        ),
        const SizedBox(height: 12),
        const Card(
          child: Padding(
            padding: EdgeInsets.all(16),
            child: Text('No hay visitas cargadas todavia.'),
          ),
        ),
      ],
    );
  }
}

class _SummaryCard extends StatelessWidget {
  const _SummaryCard({
    required this.title,
    required this.value,
    this.fullWidth = false,
  });

  final String title;
  final String value;
  final bool fullWidth;

  @override
  Widget build(BuildContext context) {
    final width = MediaQuery.of(context).size.width;
    final cardWidth = fullWidth ? double.infinity : (width - 56) / 2;
    return SizedBox(
      width: cardWidth,
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(title, style: const TextStyle(fontSize: 16)),
              const SizedBox(height: 12),
              Text(
                value,
                style: const TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _MoreTab extends StatelessWidget {
  const _MoreTab();

  @override
  Widget build(BuildContext context) {
    return ListView(
      physics: const AlwaysScrollableScrollPhysics(),
      children: [
        ListTile(
          leading: const Icon(Icons.notifications_outlined),
          title: const Text('Notificaciones'),
          onTap: () {
            Navigator.of(context).push(
              MaterialPageRoute(builder: (_) => const NotificacionesScreen()),
            );
          },
        ),
        ListTile(
          leading: const Icon(Icons.settings_outlined),
          title: const Text('Ajustes'),
          onTap: () {
            Navigator.of(context).push(
              MaterialPageRoute(builder: (_) => const SettingsScreen()),
            );
          },
        ),
        ListTile(
          leading: const Icon(Icons.logout),
          title: const Text('Cerrar sesion'),
          onTap: () => context.read<AuthProvider>().logout(),
        ),
      ],
    );
  }
}
