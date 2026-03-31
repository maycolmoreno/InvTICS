import 'dart:convert';

import 'package:http/http.dart' as http;

import '../../../core/config/app_config.dart';
import '../../../core/storage/secure_storage_service.dart';
import 'gps_models.dart';

class GpsRepository {
  GpsRepository({required SecureStorageService secureStorage})
      : _secureStorage = secureStorage;

  final SecureStorageService _secureStorage;

  Future<Map<String, String>> _headers() async {
    final token = await _secureStorage.readToken();
    return {
      'Accept': 'application/json',
      'Content-Type': 'application/json',
      if (token != null) 'Authorization': 'Basic $token',
    };
  }

  Future<void> enviarUbicacion(UbicacionTecnicoRequest request) async {
    final response = await http
        .post(
          Uri.parse('${AppConfig.baseUrl}/ubicaciones-tecnicos'),
          headers: await _headers(),
          body: jsonEncode(request.toJson()),
        )
        .timeout(const Duration(seconds: 15));

    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw Exception('Error al enviar ubicación: ${response.statusCode}');
    }
  }

  Future<void> registrarConsentimiento(ConsentimientoRequest request) async {
    final response = await http
        .post(
          Uri.parse('${AppConfig.baseUrl}/ubicaciones-tecnicos/consentimiento'),
          headers: await _headers(),
          body: jsonEncode(request.toJson()),
        )
        .timeout(const Duration(seconds: 15));

    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw Exception(_friendlyMessage(
        response,
        fallback: 'Error al registrar consentimiento: ${response.statusCode}',
      ));
    }
  }

  Future<List<UbicacionActivaResponse>> obtenerUbicacionesTiempoReal() async {
    final response = await http
        .get(
          Uri.parse('${AppConfig.baseUrl}/ubicaciones-tecnicos/tiempo-real'),
          headers: await _headers(),
        )
        .timeout(const Duration(seconds: 15));

    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw Exception('Error al consultar ubicaciones: ${response.statusCode}');
    }

    final List<dynamic> list = jsonDecode(response.body) as List<dynamic>;
    return list
        .map((e) => UbicacionActivaResponse.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  String _friendlyMessage(http.Response response, {required String fallback}) {
    if (response.body.isEmpty) {
      return fallback;
    }

    try {
      final json = jsonDecode(response.body);
      if (json is Map<String, dynamic>) {
        final message = json['message']?.toString();
        if (message != null && message.isNotEmpty) {
          return message;
        }
        final error = json['error']?.toString();
        if (error != null && error.isNotEmpty) {
          return error;
        }
      }
    } catch (_) {
      // Si la respuesta no es JSON, se usa el fallback.
    }

    return fallback;
  }
}
