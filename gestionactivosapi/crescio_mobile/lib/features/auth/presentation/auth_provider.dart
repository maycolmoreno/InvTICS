import 'dart:convert';

import 'package:flutter/foundation.dart';

import '../../../core/errors/exceptions.dart';
import '../data/auth_models.dart';
import '../data/auth_repository.dart';

enum AuthStatus {
  loading,
  authenticated,
  unauthenticated,
  requiresServerConfig,
}

class AuthProvider extends ChangeNotifier {
  AuthProvider({required AuthRepository repository}) : _repository = repository;

  final AuthRepository _repository;

  AuthStatus _status = AuthStatus.loading;
  AuthStatus get status => _status;

  AuthSession? _session;
  AuthSession? get session => _session;

  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  Future<void> bootstrap({required bool hasServerConfig}) async {
    if (!hasServerConfig) {
      _status = AuthStatus.requiresServerConfig;
      notifyListeners();
      return;
    }

    final stored = await _repository.readStoredSession();
    if (stored == null || _isExpired(stored.token)) {
      if (stored != null) {
        await _repository.logout();
        _errorMessage = 'Sesion expirada';
      }
      _status = AuthStatus.unauthenticated;
      notifyListeners();
      return;
    }

    if (stored.role.toUpperCase() != 'TECNICO') {
      await _repository.logout();
      _status = AuthStatus.unauthenticated;
      _errorMessage = 'Esta aplicacion es solo para tecnicos de soporte';
      notifyListeners();
      return;
    }

    _session = stored;
    _status = AuthStatus.authenticated;
    notifyListeners();
  }

  Future<bool> login(String username, String password) async {
    _errorMessage = null;
    _status = AuthStatus.loading;
    notifyListeners();

    try {
      final session = await _repository.login(
        LoginRequest(username: username.trim(), password: password),
      );
      if (session.role.toUpperCase() != 'TECNICO') {
        await _repository.logout();
        _status = AuthStatus.unauthenticated;
        _errorMessage = 'Esta aplicacion es solo para tecnicos de soporte';
        notifyListeners();
        return false;
      }

      _session = session;
      _status = AuthStatus.authenticated;
      notifyListeners();
      return true;
    } on AuthException catch (e) {
      _status = AuthStatus.unauthenticated;
      _errorMessage = e.message;
      notifyListeners();
      return false;
    } catch (_) {
      _status = AuthStatus.unauthenticated;
      _errorMessage = 'No fue posible iniciar sesion.';
      notifyListeners();
      return false;
    }
  }

  Future<void> logout() async {
    await _repository.logout();
    _session = null;
    _status = AuthStatus.unauthenticated;
    notifyListeners();
  }

  void markServerConfigured() {
    _status = AuthStatus.unauthenticated;
    _errorMessage = null;
    notifyListeners();
  }

  bool _isExpired(String token) {
    final parts = token.split('.');
    if (parts.length != 3) {
      return true;
    }
    try {
      final normalized = base64.normalize(parts[1]);
      final payload =
          jsonDecode(utf8.decode(base64Url.decode(normalized))) as Map<String, dynamic>;
      final exp = payload['exp'];
      if (exp is! num) {
        return true;
      }
      final expiry = DateTime.fromMillisecondsSinceEpoch(exp.toInt() * 1000);
      return DateTime.now().isAfter(expiry);
    } catch (_) {
      return true;
    }
  }
}
