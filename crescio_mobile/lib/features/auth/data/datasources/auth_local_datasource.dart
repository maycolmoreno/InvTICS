import 'dart:convert';
import '../../../core/domain/failures/failures.dart';
import '../../../core/storage/secure_storage_service.dart';
import '../models/auth_session_model.dart';

/// Interface para datasource local de autenticación
abstract class AuthLocalDatasource {
  Future<AuthSessionModel?> readStoredSession();
  Future<void> saveSession(AuthSessionModel session);
  Future<void> clearSession();
}

/// Implementación de datasource local usando SecureStorage
class AuthLocalDatasourceImpl implements AuthLocalDatasource {
  final SecureStorageService _secureStorage;

  AuthLocalDatasourceImpl(this._secureStorage);

  static const String _sessionKey = 'auth_session_json';

  @override
  Future<AuthSessionModel?> readStoredSession() async {
    try {
      final sessionJson = await _secureStorage.readToken();
      if (sessionJson == null || sessionJson.isEmpty) {
        return null;
      }

      try {
        final data = jsonDecode(sessionJson) as Map<String, dynamic>;
        return AuthSessionModel.fromJson(data);
      } catch (_) {
        // Si no puede parsear JSON moderno, intentar parsear formato antiguo
        return await _readLegacySession();
      }
    } catch (_) {
      return null;
    }
  }

  @override
  Future<void> saveSession(AuthSessionModel session) async {
    try {
      final sessionJson = jsonEncode(session.toJson());
      await _secureStorage.saveSession(
        token: sessionJson,
        username: session.user.username,
        displayName: session.user.displayName,
        role: session.user.role,
      );
    } catch (_) {
      throw StorageFailure('No se pudo guardar la sesión.');
    }
  }

  @override
  Future<void> clearSession() async {
    try {
      await _secureStorage.clearSession();
    } catch (_) {
      throw StorageFailure('No se pudo limpiar la sesión.');
    }
  }

  /// Intenta leer formato antiguo de sesión (para retrocompatibilidad)
  Future<AuthSessionModel?> _readLegacySession() async {
    try {
      final token = await _secureStorage.readToken();
      if (token == null || token.isEmpty) {
        return null;
      }

      final username = await _secureStorage.readUsername() ?? '';
      final displayName = await _secureStorage.readDisplayName() ?? username;
      final role = await _secureStorage.readRole() ?? '';

      return AuthSessionModel(
        token: token,
        refreshToken: token,
        user: AuthUserModel(
          id: username,
          username: username,
          displayName: displayName,
          email: username,
          role: role,
          permissions: [],
          isActive: true,
        ),
        expiresAt: DateTime.now().add(const Duration(hours: 24)),
        createdAt: DateTime.now(),
      );
    } catch (_) {
      return null;
    }
  }
}
