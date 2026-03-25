import 'dart:convert';
import 'package:http/http.dart' as http;

import '../../../core/config/app_config.dart';
import '../../../core/domain/failures/failures.dart';
import '../models/auth_session_model.dart';

/// Interface para datasource remoto de autenticación
abstract class AuthRemoteDatasource {
  Future<AuthSessionModel> login(String username, String password);
  Future<AuthSessionModel> refreshToken(String refreshToken);
}

/// Implementación de datasource remoto de autenticación
class AuthRemoteDatasourceImpl implements AuthRemoteDatasource {
  final http.Client _client;

  AuthRemoteDatasourceImpl(this._client);

  @override
  Future<AuthSessionModel> login(String username, String password) async {
    final basicToken = base64Encode(
      utf8.encode('$username:$password'),
    );

    try {
      final response = await _client.get(
        Uri.parse('${AppConfig.baseUrl}/auth/yo'),
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Basic $basicToken',
        },
      ).timeout(const Duration(seconds: 15));

      if (response.statusCode == 401) {
        throw AuthException('Credenciales incorrectas.');
      }
      if (response.statusCode == 403) {
        throw AuthException('Sin permisos.');
      }
      if (response.statusCode < 200 || response.statusCode >= 300) {
        throw ServerException('No fue posible iniciar sesión.');
      }

      final data = jsonDecode(response.body) as Map<String, dynamic>;
      return AuthSessionModel.fromJson({
        ...data,
        'token': basicToken,
        'refreshToken': basicToken, // Por ahora, usar el mismo token
      });
    } catch (e) {
      rethrow;
    }
  }

  @override
  Future<AuthSessionModel> refreshToken(String refreshToken) async {
    try {
      final response = await _client.post(
        Uri.parse('${AppConfig.baseUrl}/auth/refresh'),
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $refreshToken',
        },
      ).timeout(const Duration(seconds: 15));

      if (response.statusCode == 401) {
        throw SessionExpiredFailure('Token expirado.');
      }
      if (response.statusCode < 200 || response.statusCode >= 300) {
        throw ServerException('No fue posible refrescar el token.');
      }

      final data = jsonDecode(response.body) as Map<String, dynamic>;
      return AuthSessionModel.fromJson(data);
    } catch (e) {
      rethrow;
    }
  }
}
