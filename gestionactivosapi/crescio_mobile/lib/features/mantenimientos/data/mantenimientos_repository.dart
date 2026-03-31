import 'dart:io';
import 'dart:math';

import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/storage/local_database.dart';

class MantenimientosRepository {
  const MantenimientosRepository(this._apiClient);

  final ApiClient _apiClient;

  Future<List<Map<String, dynamic>>> listar() async {
    final data = await _apiClient.get('/mantenimiento');
    final items = (data as List)
        .map((item) => Map<String, dynamic>.from(item as Map))
        .toList();
    items.sort((a, b) {
      final createdCompare = _createdAtOrMin(b).compareTo(_createdAtOrMin(a));
      if (createdCompare != 0) {
        return createdCompare;
      }
      return _asInt(b['idMantenimiento'])
          .compareTo(_asInt(a['idMantenimiento']));
    });
    return items;
  }

  Future<Map<String, dynamic>> obtenerDetalle(int mantenimientoId) async {
    final data = await _apiClient.get('/mantenimiento/$mantenimientoId');
    return Map<String, dynamic>.from(data as Map);
  }

  Future<List<Map<String, dynamic>>> listarCustodios() async {
    final data = await _apiClient.get('/custodios');
    final items = (data as List)
        .map((item) => Map<String, dynamic>.from(item as Map))
        .toList();
    items.sort((a, b) => _text(a['nombre']).compareTo(_text(b['nombre'])));
    return items;
  }

  Future<List<Map<String, dynamic>>> listarCustodias() async {
    final data = await _apiClient.get('/custodias');
    return (data as List)
        .map((item) => Map<String, dynamic>.from(item as Map))
        .toList();
  }

  Future<List<Map<String, dynamic>>> listarActividadesChecklist() async {
    final data = await _apiClient.get('/actividades-checklist');
    final items = (data as List)
        .map((item) => Map<String, dynamic>.from(item as Map))
        .toList();
    items.sort((a, b) {
      final categoryCompare =
          _primeraCategoria(a).compareTo(_primeraCategoria(b));
      if (categoryCompare != 0) {
        return categoryCompare;
      }
      return _asInt(a['orden']).compareTo(_asInt(b['orden']));
    });
    return items;
  }

  Future<Map<String, dynamic>> crear({
    required int equipoId,
    required int custodioId,
    required String tipoMantenimiento,
    required String fechaMantenimiento,
    required String detalle,
    required String estadoGeneral,
    required List<Map<String, dynamic>> actividades,
    String? firmaTecnico,
    String? firmaCustodio,
  }) async {
    final data = await _apiClient.post('/mantenimiento', {
      'equipoId': equipoId,
      'custodioId': custodioId,
      'tipoMantenimiento': tipoMantenimiento,
      'fechaMantenimiento': fechaMantenimiento,
      'detalle': detalle,
      'estadoGeneral': estadoGeneral,
      'firmaTecnico': firmaTecnico,
      'firmaCustodio': firmaCustodio,
      'actividades': actividades,
      'imagenes': <Map<String, dynamic>>[],
    });
    return Map<String, dynamic>.from(data as Map);
  }

  Future<List<Map<String, dynamic>>> crearVarios({
    required List<int> equipoIds,
    required int custodioId,
    required String tipoMantenimiento,
    required String fechaMantenimiento,
    required String detalle,
    required String estadoGeneral,
    required List<Map<String, dynamic>> actividades,
    String? firmaTecnico,
    String? firmaCustodio,
  }) async {
    final created = <Map<String, dynamic>>[];
    for (final equipoId in equipoIds) {
      created.add(
        await crear(
          equipoId: equipoId,
          custodioId: custodioId,
          tipoMantenimiento: tipoMantenimiento,
          fechaMantenimiento: fechaMantenimiento,
          detalle: detalle,
          estadoGeneral: estadoGeneral,
          actividades: actividades,
          firmaTecnico: firmaTecnico,
          firmaCustodio: firmaCustodio,
        ),
      );
    }
    return created;
  }

  Future<bool> crearVariosConFallback({
    required List<int> equipoIds,
    required int custodioId,
    required String tipoMantenimiento,
    required String fechaMantenimiento,
    required String detalle,
    required String estadoGeneral,
    required List<Map<String, dynamic>> actividades,
    required List<Map<String, dynamic>> imagenes,
    String? firmaTecnico,
    String? firmaCustodio,
  }) async {
    try {
      final createdItems = await crearVarios(
        equipoIds: equipoIds,
        custodioId: custodioId,
        tipoMantenimiento: tipoMantenimiento,
        fechaMantenimiento: fechaMantenimiento,
        detalle: detalle,
        estadoGeneral: estadoGeneral,
        actividades: actividades,
        firmaTecnico: firmaTecnico,
        firmaCustodio: firmaCustodio,
      );
      if (imagenes.isNotEmpty) {
        for (final created in createdItems) {
          final mantenimientoId = _asInt(created['idMantenimiento']) == 0
              ? _asInt(created['id'])
              : _asInt(created['idMantenimiento']);
          if (mantenimientoId > 0) {
            await subirImagenes(
              mantenimientoId: mantenimientoId,
              imagenes: imagenes,
            );
          }
        }
      }
      return false;
    } on OfflineException {
      await LocalDatabase.instance.enqueueSyncOperation(
        id: _syncId('create'),
        operation: 'create_mantenimiento',
        payload: {
          'equipoIds': equipoIds,
          'custodioId': custodioId,
          'tipoMantenimiento': tipoMantenimiento,
          'fechaMantenimiento': fechaMantenimiento,
          'detalle': detalle,
          'estadoGeneral': estadoGeneral,
          'actividades': actividades,
          'imagenes': imagenes,
          'firmaTecnico': firmaTecnico,
          'firmaCustodio': firmaCustodio,
        },
      );
      return true;
    }
  }

  Future<void> guardarImagenes({
    required int mantenimientoId,
    required List<Map<String, dynamic>> imagenes,
  }) async {
    await _apiClient.post('/mantenimiento/$mantenimientoId/imagenes', imagenes);
  }

  Future<void> subirImagenes({
    required int mantenimientoId,
    required List<Map<String, dynamic>> imagenes,
  }) async {
    final files = imagenes
        .map((item) => _text(item['rutaArchivo']))
        .where((path) => path.isNotEmpty)
        .map(File.new)
        .where((file) => file.existsSync())
        .toList();
    if (files.isEmpty) {
      return;
    }
    await _apiClient.postMultipart(
      '/mantenimiento/$mantenimientoId/imagenes/upload',
      files,
      const {},
    );
  }

  Future<void> cerrar({
    required int mantenimientoId,
    required String observaciones,
  }) async {
    await _apiClient.post('/mantenimiento/cerrar/$mantenimientoId', {
      'descripcionTrabajoRealizado': observaciones,
    });
  }

  Future<void> reenviarCorreo({
    required int mantenimientoId,
  }) async {
    await _apiClient
        .post('/mantenimiento/$mantenimientoId/reenviar-correo', const {});
  }

  Future<List<int>> descargarPdf({
    required int mantenimientoId,
  }) async {
    return _apiClient.getBytes('/mantenimiento/$mantenimientoId/pdf');
  }

  Future<bool> cerrarConFallback({
    required int mantenimientoId,
    required String observaciones,
  }) async {
    try {
      await cerrar(
        mantenimientoId: mantenimientoId,
        observaciones: observaciones,
      );
      return false;
    } on OfflineException {
      await LocalDatabase.instance.enqueueSyncOperation(
        id: _syncId('close'),
        operation: 'close_mantenimiento',
        payload: {
          'mantenimientoId': mantenimientoId,
          'observaciones': observaciones,
        },
      );
      return true;
    }
  }

  Future<void> syncQueuedCreate(Map<String, dynamic> payload) async {
    final equipoIds = (payload['equipoIds'] as List? ?? const [])
        .map((item) => _asInt(item))
        .where((item) => item > 0)
        .toList();
    if (equipoIds.isEmpty) {
      throw const ServerException(
          'No hay equipos pendientes para sincronizar.');
    }
    final actividades = (payload['actividades'] as List? ?? const [])
        .map((item) => Map<String, dynamic>.from(item as Map))
        .toList();
    final imagenes = (payload['imagenes'] as List? ?? const [])
        .map((item) => Map<String, dynamic>.from(item as Map))
        .toList();
    final createdItems = await crearVarios(
      equipoIds: equipoIds,
      custodioId: _asInt(payload['custodioId']),
      tipoMantenimiento: _text(payload['tipoMantenimiento']),
      fechaMantenimiento: _text(payload['fechaMantenimiento']),
      detalle: _text(payload['detalle']),
      estadoGeneral: _text(payload['estadoGeneral']),
      actividades: actividades,
      firmaTecnico: _text(payload['firmaTecnico'], fallback: ''),
      firmaCustodio: _text(payload['firmaCustodio'], fallback: ''),
    );
    if (imagenes.isNotEmpty) {
      for (final created in createdItems) {
        final mantenimientoId = _asInt(created['idMantenimiento']) == 0
            ? _asInt(created['id'])
            : _asInt(created['idMantenimiento']);
        if (mantenimientoId > 0) {
          await subirImagenes(
            mantenimientoId: mantenimientoId,
            imagenes: imagenes,
          );
        }
      }
    }
  }

  Future<void> syncQueuedClose(Map<String, dynamic> payload) async {
    await cerrar(
      mantenimientoId: _asInt(payload['mantenimientoId']),
      observaciones: _text(payload['observaciones']),
    );
  }
}

String _text(dynamic value, {String fallback = ''}) {
  final text = value?.toString().trim() ?? '';
  return text.isEmpty ? fallback : text;
}

/// Extrae la primera categoría de la lista `categorias` del JSON del backend.
String _primeraCategoria(Map<String, dynamic> item, {String fallback = ''}) {
  final categorias = item['categorias'];
  if (categorias is List && categorias.isNotEmpty) {
    return _text(categorias.first, fallback: fallback);
  }
  return _text(item['categoria'], fallback: fallback);
}

int _asInt(dynamic value) {
  if (value is int) {
    return value;
  }
  if (value is num) {
    return value.toInt();
  }
  return int.tryParse(value?.toString() ?? '') ?? 0;
}

String _syncId(String prefix) {
  final random = Random().nextInt(1 << 32).toRadixString(16);
  return '$prefix-${DateTime.now().microsecondsSinceEpoch}-$random';
}

DateTime _createdAtOrMin(Map<String, dynamic> item) {
  final raw = _text(item['creadoEn']);
  if (raw.isEmpty) {
    return DateTime.fromMillisecondsSinceEpoch(0);
  }
  return DateTime.tryParse(raw) ?? DateTime.fromMillisecondsSinceEpoch(0);
}
