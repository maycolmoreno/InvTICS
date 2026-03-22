import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/foundation.dart';

import '../../../core/storage/local_database.dart';

class DashboardProvider extends ChangeNotifier {
  DashboardProvider() {
    _init();
  }

  bool _offline = false;
  int _pendingOffline = 0;

  bool get offline => _offline;
  int get pendingOffline => _pendingOffline;

  Future<void> _init() async {
    await refresh();
  }

  Future<void> refresh() async {
    final connectivity = await Connectivity().checkConnectivity();
    _offline = connectivity.contains(ConnectivityResult.none);
    _pendingOffline = await LocalDatabase.instance.contarPendientes();
    notifyListeners();
  }
}
