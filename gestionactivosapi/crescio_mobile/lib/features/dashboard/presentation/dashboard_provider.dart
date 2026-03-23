import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/foundation.dart';

import '../../../core/network/api_client.dart';
import '../../../core/storage/local_database.dart';
import '../../mantenimientos/data/mantenimientos_repository.dart';
import '../../notificaciones/data/notificaciones_repository.dart';
import '../../sync/sync_service.dart';

class DashboardProvider extends ChangeNotifier {
  DashboardProvider(this._apiClient) {
    _init();
  }

  final ApiClient _apiClient;

  bool _offline = false;
  int _pendingOffline = 0;
  int _pendingNotifications = 0;
  int _openMantenimientos = 0;
  int _activeEquipos = 0;
  List<Map<String, dynamic>> _recentMantenimientos = const [];

  bool get offline => _offline;
  int get pendingOffline => _pendingOffline;
  int get pendingNotifications => _pendingNotifications;
  int get openMantenimientos => _openMantenimientos;
  int get activeEquipos => _activeEquipos;
  List<Map<String, dynamic>> get recentMantenimientos => _recentMantenimientos;

  Future<void> _init() async {
    await refresh();
  }

  Future<void> refresh() async {
    final connectivity = await Connectivity().checkConnectivity();
    _offline = connectivity.contains(ConnectivityResult.none);
    _pendingOffline = await LocalDatabase.instance.contarPendientes();

    if (_offline) {
      _pendingNotifications = 0;
      _openMantenimientos = 0;
      _activeEquipos = 0;
      _recentMantenimientos = const [];
      notifyListeners();
      return;
    }

    await SyncService(_apiClient).syncPendingOperations();
    _pendingOffline = await LocalDatabase.instance.contarPendientes();

    final notificacionesRepository = NotificacionesRepository(_apiClient);
    final mantenimientosRepository = MantenimientosRepository(_apiClient);
    final equiposRaw = await _apiClient.get('/equipos');
    final equipos = (equiposRaw as List)
        .map((item) => Map<String, dynamic>.from(item as Map))
        .toList();
    final mantenimientos = await mantenimientosRepository.listar();

    _pendingNotifications = await notificacionesRepository.obtenerConteo();
    _openMantenimientos = mantenimientos
        .where((item) => _text(item['estadoInterno']).toUpperCase() != 'CERRADO')
        .length;
    _activeEquipos = equipos
        .where((item) => _text(item['estadoEquipo']).toUpperCase() != 'BAJA')
        .length;

    final recent = List<Map<String, dynamic>>.from(mantenimientos)
      ..sort(
        (a, b) => _text(b['fechaMantenimiento']).compareTo(_text(a['fechaMantenimiento'])),
      );
    _recentMantenimientos = recent.take(4).toList();
    notifyListeners();
  }
}

String _text(dynamic value, {String fallback = ''}) {
  final text = value?.toString().trim() ?? '';
  return text.isEmpty ? fallback : text;
}
