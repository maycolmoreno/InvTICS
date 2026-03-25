import 'package:equatable/equatable.dart';

/// Estado del equipo
enum EquipmentStatus {
  activo,
  inactivo,
  mantenimiento,
  desechado,
}

/// Equipo/Activo - entidad de dominio
class Equipment extends Equatable {
  final String id;
  final String codigo;
  final String nombre;
  final String? descripcion;
  final String? modelo;
  final String? serie;
  final String? marca;
  final EquipmentStatus estado;
  final String? ubicacion;
  final String? custodio;
  final DateTime? fechaAdquisicion;
  final String? caracteristicas;
  final double? latitud;
  final double? longitud;
  final DateTime createdAt;
  final DateTime? updatedAt;

  const Equipment({
    required this.id,
    required this.codigo,
    required this.nombre,
    this.descripcion,
    this.modelo,
    this.serie,
    this.marca,
    required this.estado,
    this.ubicacion,
    this.custodio,
    this.fechaAdquisicion,
    this.caracteristicas,
    this.latitud,
    this.longitud,
    required this.createdAt,
    this.updatedAt,
  });

  @override
  List<Object?> get props => [
        id,
        codigo,
        nombre,
        descripcion,
        modelo,
        serie,
        marca,
        estado,
        ubicacion,
        custodio,
        fechaAdquisicion,
        caracteristicas,
        latitud,
        longitud,
        createdAt,
        updatedAt,
      ];
}

/// Filtro para búsqueda de equipos
class EquipmentFilter extends Equatable {
  final String? search;
  final EquipmentStatus? estado;
  final String? ubicacion;
  final String? custodio;
  final int page;
  final int pageSize;

  const EquipmentFilter({
    this.search,
    this.estado,
    this.ubicacion,
    this.custodio,
    this.page = 1,
    this.pageSize = 20,
  });

  @override
  List<Object?> get props => [
        search,
        estado,
        ubicacion,
        custodio,
        page,
        pageSize,
      ];
}

/// Respuesta paginada de equipos
class EquipmentList extends Equatable {
  final List<Equipment> items;
  final int totalItems;
  final int totalPages;
  final int currentPage;

  const EquipmentList({
    required this.items,
    required this.totalItems,
    required this.totalPages,
    required this.currentPage,
  });

  @override
  List<Object?> get props => [items, totalItems, totalPages, currentPage];
}
