import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../core/network/api_client.dart';
import '../../../shared/widgets/cresio_scaffold.dart';
import '../../../shared/widgets/status_banner.dart';
import '../../auth/data/auth_models.dart';
import '../../auth/presentation/auth_provider.dart';
import '../../configuracion/presentation/settings_screen.dart';
import '../../equipos/presentation/equipos_screen.dart';
import '../../mantenimientos/presentation/mantenimientos_screen.dart';
import '../../notificaciones/presentation/notificaciones_screen.dart';
import '../../ubicaciones/presentation/ubicaciones_screen.dart';
import '../../visitas/presentation/visitas_screen.dart';
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
      create: (context) => DashboardProvider(context.read<ApiClient>()),
      child: Consumer<DashboardProvider>(
        builder: (context, dashboard, _) {
          final auth = context.watch<AuthProvider>();
          final tabs = <_ShellTab>[
            _ShellTab(
              title: 'CRESIO Mobile',
              destination: const NavigationDestination(
                icon: Icon(Icons.home_outlined),
                label: 'Inicio',
              ),
              page: _HomeTab(
                pendingNotifications: dashboard.pendingNotifications,
                openMantenimientos: dashboard.openMantenimientos,
                activeEquipos: dashboard.activeEquipos,
                recentMantenimientos: dashboard.recentMantenimientos,
                pendingOffline: dashboard.pendingOffline,
                offline: dashboard.offline,
              ),
            ),
          ];
          if (auth.hasCapability(UserCapability.viewMantenimientos)) {
            tabs.add(
              const _ShellTab(
                title: 'Mantenimientos',
                destination: NavigationDestination(
                  icon: Icon(Icons.build_outlined),
                  label: 'Mantenimientos',
                ),
                page: MantenimientosScreen(),
              ),
            );
          }
          if (auth.hasCapability(UserCapability.viewVisitas)) {
            tabs.add(
              _ShellTab(
                title: 'Visita tecnica',
                destination: NavigationDestination(
                  icon: Badge(
                    isLabelVisible: dashboard.pendingNotifications > 0,
                    label: Text('${dashboard.pendingNotifications}'),
                    child: const Icon(Icons.assignment_outlined),
                  ),
                  label: 'Visitas',
                ),
                page: const VisitasScreen(),
              ),
            );
          }
          if (auth.hasCapability(UserCapability.viewEquipos)) {
            tabs.add(
              const _ShellTab(
                title: 'Equipos',
                destination: NavigationDestination(
                  icon: Icon(Icons.computer_outlined),
                  label: 'Equipos',
                ),
                page: EquiposScreen(),
              ),
            );
          }
          tabs.add(
            const _ShellTab(
              title: 'Mas opciones',
              destination: NavigationDestination(
                icon: Icon(Icons.more_horiz),
                label: 'Mas',
              ),
              page: _MoreTab(),
            ),
          );
          final currentIndex = _index >= tabs.length ? tabs.length - 1 : _index;

          return CresioScaffold(
            title: tabs[currentIndex].title,
            bottomNavigationBar: NavigationBar(
              selectedIndex: currentIndex,
              onDestinationSelected: (value) => setState(() => _index = value),
              destinations: tabs.map((tab) => tab.destination).toList(),
            ),
            floatingActionButton: currentIndex == 0 &&
                    auth.hasCapability(UserCapability.createMantenimiento)
                ? FloatingActionButton.extended(
                    onPressed: () => setState(
                      () => _index = tabs.indexWhere(
                        (tab) => tab.title == 'Mantenimientos',
                      ),
                    ),
                    icon: const Icon(Icons.add),
                    label: const Text('+Mantenimiento'),
                  )
                : null,
            child: RefreshIndicator(
              onRefresh: dashboard.refresh,
              child: tabs[currentIndex].page,
            ),
          );
        },
      ),
    );
  }
}

class _ShellTab {
  const _ShellTab({
    required this.title,
    required this.destination,
    required this.page,
  });

  final String title;
  final NavigationDestination destination;
  final Widget page;
}

class _HomeTab extends StatelessWidget {
  const _HomeTab({
    required this.pendingNotifications,
    required this.openMantenimientos,
    required this.activeEquipos,
    required this.recentMantenimientos,
    required this.pendingOffline,
    required this.offline,
  });

  final int pendingNotifications;
  final int openMantenimientos;
  final int activeEquipos;
  final List<Map<String, dynamic>> recentMantenimientos;
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
          children: [
            _SummaryCard(title: 'Mantenimientos abiertos', value: '$openMantenimientos'),
            _SummaryCard(title: 'Equipos activos', value: '$activeEquipos'),
            _SummaryCard(title: 'Notificaciones', value: '$pendingNotifications'),
          ],
        ),
        const SizedBox(height: 12),
        _SummaryCard(title: 'Pendientes offline', value: '$pendingOffline', fullWidth: true),
        const SizedBox(height: 20),
        const Text(
          'Actividad reciente',
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.w700),
        ),
        const SizedBox(height: 12),
        if (recentMantenimientos.isEmpty)
          const Card(
            child: Padding(
              padding: EdgeInsets.all(16),
              child: Text('No hay actividad reciente disponible.'),
            ),
          )
        else
          ...recentMantenimientos.map(
            (item) => Card(
              child: ListTile(
                leading: const Icon(Icons.build_circle_outlined),
                title: Text(_text(item['equipoCodigoSap'], fallback: 'Sin codigo')),
                subtitle: Text(
                  '${_text(item['equipoDescripcion'])}\n'
                  'Estado: ${_text(item['estadoInterno'])}',
                ),
                isThreeLine: true,
                trailing: Text(_text(item['fechaMantenimiento'], fallback: '-')),
              ),
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
    final auth = context.watch<AuthProvider>();
    return ListView(
      physics: const AlwaysScrollableScrollPhysics(),
      children: [
        if (auth.hasCapability(UserCapability.viewVisitas))
          ListTile(
            leading: const Icon(Icons.assignment_outlined),
            title: const Text('Visita tecnica'),
            onTap: () {
              Navigator.of(context).push(
                MaterialPageRoute(builder: (_) => const VisitasScreen()),
              );
            },
          ),
        if (auth.hasCapability(UserCapability.viewNotificaciones))
          ListTile(
            leading: const Icon(Icons.notifications_outlined),
            title: const Text('Notificaciones'),
            onTap: () {
              Navigator.of(context).push(
                MaterialPageRoute(builder: (_) => const NotificacionesScreen()),
              );
            },
          ),
        if (auth.hasCapability(UserCapability.manageUbicaciones))
          ListTile(
            leading: const Icon(Icons.location_city_outlined),
            title: const Text('Ubicaciones'),
            onTap: () {
              Navigator.of(context).push(
                MaterialPageRoute(builder: (_) => const UbicacionesScreen()),
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

String _text(dynamic value, {String fallback = '-'}) {
  final text = value?.toString().trim() ?? '';
  return text.isEmpty ? fallback : text;
}
