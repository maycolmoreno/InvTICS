import 'dart:convert';

import 'package:http/http.dart' as http;

import '../../../core/config/app_config.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/storage/secure_storage_service.dart';
import 'auth_models.dart';

class AuthRepository {
  AuthRepository({
    required SecureStorageService secureStorage,
  }) : _secureStorage = secureStorage;

  final SecureStorageService _secureStorage;

  Future<AuthSession> login(LoginRequest request) async {
    final basicToken = base64Encode(
      utf8.encode('${request.username}:${request.password}'),
    );
    final uri = Uri.parse('${AppConfig.baseUrl}/autenticacion/login').replace(
      queryParameters: {
        'correo': request.username,
        'contrasena': request.password,
      },
    );

    final response = await http
        .post(
          uri,
          headers: const {
            'Accept': 'application/json',
          },
        )
        .timeout(const Duration(seconds: 15));

    if (response.statusCode == 401) {
      throw const AuthException('Credenciales incorrectas.');
    }
    if (response.statusCode == 403) {
      throw const AuthException('Sin permisos.');
    }
    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw const AuthException('No fue posible iniciar sesion.');
    }

    final data = jsonDecode(response.body) as Map<String, dynamic>;
    final session = AuthSession.fromJson({
      ...data,
      'token': basicToken,
    });
    await _secureStorage.saveSession(
      token: session.token,
      username: session.username,
      displayName: session.displayName,
      role: session.role,
    );
    return session;
  }

  Future<void> logout() => _secureStorage.clearSession();

  Future<AuthSession?> readStoredSession() async {
    final token = await _secureStorage.readToken();
    if (token == null || token.isEmpty) {
      return null;
    }
    final username = await _secureStorage.readUsername() ?? '';
    final displayName = await _secureStorage.readDisplayName() ?? username;
    final role = await _secureStorage.readRole() ?? '';
    return AuthSession(
      token: token,
      username: username,
      displayName: displayName,
      role: role,
    );
  }
}
