import 'dart:async';
import 'dart:convert';
import 'dart:io';

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
    late final http.Response response;
    try {
      response = await http.get(
        Uri.parse('${AppConfig.baseUrl}/auth/yo'),
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Basic $basicToken',
        },
      ).timeout(const Duration(seconds: 15));
    } on TimeoutException {
      throw const AuthException(
        'No fue posible validar el usuario. Verifica tus credenciales y la conexion con el servidor.',
      );
    } on SocketException {
      throw const OfflineException(
        'No fue posible conectar con el servidor. Revisa la URL configurada y tu conexion.',
      );
    } on http.ClientException {
      throw const OfflineException(
        'No fue posible conectar con el servidor. Revisa la URL configurada y tu conexion.',
      );
    }

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
    final rawUserId = data['idUsuario'];
    final userId = rawUserId is int
        ? rawUserId
        : int.tryParse(rawUserId?.toString() ?? '');
    final session = AuthSession(
      token: basicToken,
      username: data['correo']?.toString() ?? request.username,
      displayName: data['nombreUsuario']?.toString() ?? request.username,
      role: data['rol']?.toString() ?? '',
      modules: _parseModules(data['modulos']),
      modulesLoaded: data.containsKey('modulos'),
      userId: userId,
    );
    await _persistSession(session);
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
    final userIdStr = await _secureStorage.readUserId();
    final modules = await _secureStorage.readModules();
    final modulesLoaded = await _secureStorage.readModulesLoaded();
    final userId = userIdStr != null ? int.tryParse(userIdStr) : null;
    return AuthSession(
      token: token,
      username: username,
      displayName: displayName,
      role: role,
      userId: userId,
      modules: modules,
      modulesLoaded: modulesLoaded,
    );
  }

  Future<AuthSession> refreshSession(AuthSession current) async {
    late final http.Response response;
    try {
      response = await http.get(
        Uri.parse('${AppConfig.baseUrl}/auth/yo'),
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Basic ${current.token}',
        },
      ).timeout(const Duration(seconds: 15));
    } on TimeoutException {
      throw const AuthException(
        'No fue posible actualizar la sesion. Intenta nuevamente.',
      );
    } on SocketException {
      throw const OfflineException(
        'No fue posible conectar con el servidor. Revisa tu conexion.',
      );
    } on http.ClientException {
      throw const OfflineException(
        'No fue posible conectar con el servidor. Revisa tu conexion.',
      );
    }

    if (response.statusCode == 401) {
      throw const AuthException('Tu sesion ha expirado.');
    }
    if (response.statusCode == 403) {
      throw const AuthException('Sin permisos.');
    }
    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw const AuthException('No fue posible actualizar la sesion.');
    }

    final data = jsonDecode(response.body) as Map<String, dynamic>;
    final rawUserId = data['idUsuario'];
    final userId = rawUserId is int
        ? rawUserId
        : int.tryParse(rawUserId?.toString() ?? '');

    final session = AuthSession(
      token: current.token,
      username: data['correo']?.toString() ?? current.username,
      displayName: data['nombreUsuario']?.toString() ?? current.displayName,
      role: data['rol']?.toString() ?? current.role,
      userId: userId ?? current.userId,
      modules: _parseModules(data['modulos']),
      modulesLoaded: data.containsKey('modulos'),
    );

    await _persistSession(session);
    return session;
  }

  Future<void> _persistSession(AuthSession session) {
    return _secureStorage.saveSession(
      token: session.token,
      username: session.username,
      displayName: session.displayName,
      role: session.role,
      userId: session.userId?.toString(),
      modules: session.modules,
      modulesLoaded: session.modulesLoaded,
    );
  }

  List<String> _parseModules(dynamic rawModules) {
    if (rawModules is! List) {
      return const [];
    }
    return rawModules
        .map((item) => item?.toString().trim() ?? '')
        .where((item) => item.isNotEmpty)
        .toList();
  }
}
