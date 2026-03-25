import 'package:equatable/equatable.dart';

/// Coordenadas geoespaciales
class GeoCoordinates extends Equatable {
  final double latitude;
  final double longitude;
  final double? altitude;
  final double? accuracy;

  const GeoCoordinates({
    required this.latitude,
    required this.longitude,
    this.altitude,
    this.accuracy,
  });

  /// Valida que las coordenadas sean válidas
  bool get isValid {
    return latitude >= -90 &&
        latitude <= 90 &&
        longitude >= -180 &&
        longitude <= 180;
  }

  @override
  List<Object?> get props => [latitude, longitude, altitude, accuracy];
}

/// Ubicación con información de dirección
class Location extends Equatable {
  final String id;
  final String nombre;
  final String? descripcion;
  final String? direccion;
  final String? ciudad;
  final String? codigo;
  final GeoCoordinates? coordenadas;
  final DateTime createdAt;
  final DateTime? updatedAt;

  const Location({
    required this.id,
    required this.nombre,
    this.descripcion,
    this.direccion,
    this.ciudad,
    this.codigo,
    this.coordenadas,
    required this.createdAt,
    this.updatedAt,
  });

  @override
  List<Object?> get props => [
        id,
        nombre,
        descripcion,
        direccion,
        ciudad,
        codigo,
        coordenadas,
        createdAt,
        updatedAt,
      ];
}

/// Página de resultados paginados genérica
class Page<T> extends Equatable {
  final List<T> content;
  final int number;
  final int size;
  final int totalElements;
  final int totalPages;

  const Page({
    required this.content,
    required this.number,
    required this.size,
    required this.totalElements,
    required this.totalPages,
  });

  bool get isFirst => number == 0;
  bool get isLast => number >= totalPages - 1;
  bool get hasNext => !isLast;
  bool get hasPrevious => !isFirst;

  @override
  List<Object?> get props => [
        content,
        number,
        size,
        totalElements,
        totalPages,
      ];
}

/// Syncronización - entidad para operaciones offline
class SyncOperation extends Equatable {
  final String id;
  final String operationType; // 'create', 'update', 'delete'
  final String entityType; // 'equipment', 'maintenance'
  final String entityId;
  final Map<String, dynamic> payload;
  final DateTime createdAt;
  final DateTime? processedAt;
  final bool isProcessed;
  final String? errorMessage;

  const SyncOperation({
    required this.id,
    required this.operationType,
    required this.entityType,
    required this.entityId,
    required this.payload,
    required this.createdAt,
    this.processedAt,
    this.isProcessed = false,
    this.errorMessage,
  });

  @override
  List<Object?> get props => [
        id,
        operationType,
        entityType,
        entityId,
        payload,
        createdAt,
        processedAt,
        isProcessed,
        errorMessage,
      ];
}
