import 'package:flutter/foundation.dart';

import '../../../core/domain/usecases/auth/get_stored_session_usecase.dart';
import '../../../core/domain/usecases/auth/login_usecase.dart';
import '../../../core/domain/usecases/auth/logout_usecase.dart';
import '../../../core/domain/entities/auth_entity.dart';
import '../data/auth_models.dart';

enum AuthStatus {
  loading,
  authenticated,
  unauthenticated,
  requiresServerConfig,
}

class AuthProvider extends ChangeNotifier {
  AuthProvider({
    required LoginUseCase loginUseCase,
    required LogoutUseCase logoutUseCase,
    required GetStoredSessionUseCase getStoredSessionUseCase,
  })  : _loginUseCase = loginUseCase,
        _logoutUseCase = logoutUseCase,
        _getStoredSessionUseCase = getStoredSessionUseCase;

  final LoginUseCase _loginUseCase;
  final LogoutUseCase _logoutUseCase;
  final GetStoredSessionUseCase _getStoredSessionUseCase;

  AuthStatus _status = AuthStatus.loading;
  AuthStatus get status => _status;

  AuthSession? _session;
  AuthSession? get session => _session;
  bool get isAuthenticated => _session != null;
  String get roleLabel => _session?.roleLabel ?? 'Sin rol';

  bool hasCapability(UserCapability capability) {
    return _session?.capabilities.has(capability) ?? false;
  }

  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  Future<void> bootstrap({required bool hasServerConfig}) async {
    if (!hasServerConfig) {
      _status = AuthStatus.requiresServerConfig;
      notifyListeners();
      return;
    }

    final stored = await _getStoredSessionUseCase();
    if (stored == null) {
      _status = AuthStatus.unauthenticated;
      notifyListeners();
      return;
    }

    // Convertir AuthSession (entity) a AuthSession (model) si es necesario
    _session = AuthSession(
      token: stored.token,
      username: stored.user.username,
      displayName: stored.user.displayName,
      role: stored.user.role,
    );
    _status = AuthStatus.authenticated;
    notifyListeners();
  }

  Future<bool> login(String username, String password) async {
    _errorMessage = null;
    _status = AuthStatus.loading;
    notifyListeners();

    try {
      final credentials = LoginCredentials(username: username.trim(), password: password);
      final result = await _loginUseCase(credentials);

      return result.fold(
        (failure) {
          _status = AuthStatus.unauthenticated;
          _errorMessage = failure.message;
          notifyListeners();
          return false;
        },
        (session) {
          // Convertir a AuthSession model
          _session = AuthSession(
            token: session.token,
            username: session.user.username,
            displayName: session.user.displayName,
            role: session.user.role,
          );
          _status = AuthStatus.authenticated;
          notifyListeners();
          return true;
        },
      );
    } catch (e) {
      _status = AuthStatus.unauthenticated;
      _errorMessage = 'No fue posible iniciar sesión.';
      notifyListeners();
      return false;
    }
  }

  Future<void> logout() async {
    final result = await _logoutUseCase();
    result.fold(
      (failure) {
        _errorMessage = failure.message;
      },
      (_) {
        _session = null;
        _status = AuthStatus.unauthenticated;
      },
    );
    notifyListeners();
  }
}

  void markServerConfigured() {
    _status = AuthStatus.unauthenticated;
    _errorMessage = null;
    notifyListeners();
  }
}
