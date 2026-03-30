import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:http/http.dart' as http;

import '../config/app_config.dart';
import '../errors/exceptions.dart';
import '../storage/secure_storage_service.dart';

typedef UnauthorizedHandler = Future<void> Function();

class ApiClient {
  ApiClient({
    http.Client? client,
    required SecureStorageService secureStorage,
    required UnauthorizedHandler onUnauthorized,
  })  : _client = client ?? http.Client(),
        _secureStorage = secureStorage,
        _onUnauthorized = onUnauthorized,
        _isRefreshingToken = false,
        _refreshTokenCompleter = null;

  final http.Client _client;
  final SecureStorageService _secureStorage;
  final UnauthorizedHandler _onUnauthorized;
  bool _isRefreshingToken;
  Completer<String?>? _refreshTokenCompleter;

  static const _timeout = Duration(seconds: 15);

  Future<dynamic> get(String path) => _send('GET', path);

  Future<dynamic> post(String path, dynamic body) =>
      _send('POST', path, body: body);

  Future<dynamic> put(String path, dynamic body) =>
      _send('PUT', path, body: body);

  Future<dynamic> delete(String path) => _send('DELETE', path);

  Future<dynamic> postMultipart(
    String path,
    List<File> files,
    Map<String, String> fields,
  ) async {
    await _ensureConnectivity();
    final uri = Uri.parse('${AppConfig.baseUrl}$path');
    final request = http.MultipartRequest('POST', uri);
    request.headers.addAll(await _headers(includeJson: false));
    request.fields.addAll(fields);
    for (final file in files) {
      request.files.add(await http.MultipartFile.fromPath('files', file.path));
    }

    try {
      final streamed = await request.send().timeout(_timeout);
      final response = await http.Response.fromStream(streamed);
      return _parseResponse(response);
    } on SocketException {
      throw const OfflineException();
    } on TimeoutException {
      throw const OfflineException('No fue posible conectar con el servidor.');
    }
  }

  Future<dynamic> _send(
    String method,
    String path, {
    dynamic body,
  }) async {
    await _ensureConnectivity();
    final uri = Uri.parse('${AppConfig.baseUrl}$path');
    var headers = await _headers();

    try {
      late http.Response response;
      switch (method) {
        case 'POST':
          response = await _client
              .post(uri, headers: headers, body: jsonEncode(body ?? {}))
              .timeout(_timeout);
          break;
        case 'PUT':
          response = await _client
              .put(uri, headers: headers, body: jsonEncode(body ?? {}))
              .timeout(_timeout);
          break;
        case 'DELETE':
          response =
              await _client.delete(uri, headers: headers).timeout(_timeout);
          break;
        default:
          response = await _client.get(uri, headers: headers).timeout(_timeout);
      }

      // Si obtenemos 401, intentar refrescar token y reintentar
      if (response.statusCode == 401 &&
          path != '/auth/refresh' &&
          path != '/auth/yo') {
        final newToken = await _refreshTokenIfNeeded();
        if (newToken != null) {
          // Reintentar con nuevo token
          headers = await _headers();
          switch (method) {
            case 'POST':
              response = await _client
                  .post(uri, headers: headers, body: jsonEncode(body ?? {}))
                  .timeout(_timeout);
              break;
            case 'PUT':
              response = await _client
                  .put(uri, headers: headers, body: jsonEncode(body ?? {}))
                  .timeout(_timeout);
              break;
            case 'DELETE':
              response =
                  await _client.delete(uri, headers: headers).timeout(_timeout);
              break;
            default:
              response =
                  await _client.get(uri, headers: headers).timeout(_timeout);
          }
        }
      }

      return _parseResponse(response);
    } on SocketException {
      throw const OfflineException();
    } on TimeoutException {
      throw const OfflineException('Tiempo de espera agotado.');
    }
  }

  /// Intenta refrescar el token si está expirado
  Future<String?> _refreshTokenIfNeeded() async {
    // Si ya estamos refrescando, esperar a que se complete
    if (_isRefreshingToken) {
      _refreshTokenCompleter ??= Completer<String?>();
      return await _refreshTokenCompleter!.future;
    }

    _isRefreshingToken = true;
    _refreshTokenCompleter = Completer<String?>();

    try {
      final currentToken = await _secureStorage.readToken();
      if (currentToken == null || currentToken.isEmpty) {
        _isRefreshingToken = false;
        _refreshTokenCompleter!.complete(null);
        return null;
      }

      // Intentar refrescar el token
      // NOTA: Este endpoint debe ser implementado en gestionactivosapi
      final uri = Uri.parse('${AppConfig.baseUrl}/auth/refresh');
      final headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $currentToken',
      };

      final response =
          await _client.post(uri, headers: headers).timeout(_timeout);

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body) as Map<String, dynamic>;
        final newToken = data['token']?.toString();
        if (newToken != null && newToken.isNotEmpty) {
          // Guardar nuevo token
          await _secureStorage.saveSession(
            token: newToken,
            username: data['username']?.toString() ?? '',
            displayName: data['displayName']?.toString() ?? '',
            role: data['role']?.toString() ?? '',
          );
          _isRefreshingToken = false;
          _refreshTokenCompleter!.complete(newToken);
          return newToken;
        }
      }

      // Si falla el refresh, logout
      await _secureStorage.clearSession();
      unawaited(_onUnauthorized());
      _isRefreshingToken = false;
      _refreshTokenCompleter!.complete(null);
      return null;
    } catch (_) {
      await _secureStorage.clearSession();
      unawaited(_onUnauthorized());
      _isRefreshingToken = false;
      _refreshTokenCompleter!.complete(null);
      return null;
    }
  }

  Future<void> _ensureConnectivity() async {
    final results = await Connectivity().checkConnectivity();
    if (results.contains(ConnectivityResult.none)) {
      throw const OfflineException();
    }
  }

  Future<Map<String, String>> _headers({bool includeJson = true}) async {
    final token = await _secureStorage.readToken();
    return {
      'Accept': 'application/json',
      if (includeJson) 'Content-Type': 'application/json',
      if (token != null && token.isNotEmpty) 'Authorization': 'Basic $token',
    };
  }

  dynamic _parseResponse(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      if (response.body.isEmpty) {
        return null;
      }
      return jsonDecode(response.body);
    }

    if (response.statusCode == 401) {
      _secureStorage.clearSession();
      unawaited(_onUnauthorized());
      throw const AuthException('Sesion expirada. Inicia sesion nuevamente.');
    }
    if (response.statusCode == 403) {
      throw const AuthException('Sin permisos.');
    }
    if (response.statusCode == 404) {
      throw const NotFoundException();
    }
    if (response.statusCode >= 500) {
      throw const ServerException();
    }
    throw ServerException(_friendlyMessage(response.body));
  }

  String _friendlyMessage(String body) {
    if (body.isEmpty) {
      return 'No fue posible completar la operacion.';
    }
    try {
      final json = jsonDecode(body);
      return json['message']?.toString() ??
          json['error']?.toString() ??
          'No fue posible completar la operacion.';
    } catch (_) {
      return 'No fue posible completar la operacion.';
    }
  }
}
