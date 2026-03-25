import 'package:equatable/equatable.dart';

/// Estado del mantenimiento
enum MaintenanceStatus {
  pendiente,
  enProgreso,
  completado,
  cancelado,
}

/// Tipo de mantenimiento
enum MaintenanceType {
  preventivo,
  correctivo,
  inspección,
}

/// Mantenimiento - entidad de dominio
class Maintenance extends Equatable {
  final String id;
  final String codigo;
  final String equipoId;
  final String? equipoCodigo;
  final String? equipoNombre;
  final MaintenanceType tipo;
  final MaintenanceStatus estado;
  final String descripcion;
  final String? observaciones;
  final String? tecnicoAsignado;
  final String? tecnicoNombre;
  final DateTime? fechaProgamada;
  final DateTime? fechaInicio;
  final DateTime? fechaFin;
  final double? costo;
  final List<String>? partesUtilizadas;
  final DateTime createdAt;
  final DateTime? updatedAt;

  const Maintenance({
    required this.id,
    required this.codigo,
    required this.equipoId,
    this.equipoCodigo,
    this.equipoNombre,
    required this.tipo,
    required this.estado,
    required this.descripcion,
    this.observaciones,
    this.tecnicoAsignado,
    this.tecnicoNombre,
    this.fechaProgamada,
    this.fechaInicio,
    this.fechaFin,
    this.costo,
    this.partesUtilizadas,
    required this.createdAt,
    this.updatedAt,
  });

  @override
  List<Object?> get props => [
        id,
        codigo,
        equipoId,
        equipoCodigo,
        equipoNombre,
        tipo,
        estado,
        descripcion,
        observaciones,
        tecnicoAsignado,
        tecnicoNombre,
        fechaProgamada,
        fechaInicio,
        fechaFin,
        costo,
        partesUtilizadas,
        createdAt,
        updatedAt,
      ];
}

/// Parámetros para crear mantenimiento
class CreateMaintenanceParams extends Equatable {
  final String equipoId;
  final MaintenanceType tipo;
  final String descripcion;
  final String? observaciones;
  final DateTime? fechaProgramada;

  const CreateMaintenanceParams({
    required this.equipoId,
    required this.tipo,
    required this.descripcion,
    this.observaciones,
    this.fechaProgramada,
  });

  @override
  List<Object?> get props => [
        equipoId,
        tipo,
        descripcion,
        observaciones,
        fechaProgramada,
      ];
}

/// Filtro para búsqueda de mantenimientos
class MaintenanceFilter extends Equatable {
  final String? search;
  final MaintenanceStatus? estado;
  final MaintenanceType? tipo;
  final String? equipoId;
  final String? tecnicoId;
  final DateTime? dateRangeStart;
  final DateTime? dateRangeEnd;
  final int page;
  final int pageSize;

  const MaintenanceFilter({
    this.search,
    this.estado,
    this.tipo,
    this.equipoId,
    this.tecnicoId,
    this.dateRangeStart,
    this.dateRangeEnd,
    this.page = 1,
    this.pageSize = 20,
  });

  @override
  List<Object?> get props => [
        search,
        estado,
        tipo,
        equipoId,
        tecnicoId,
        dateRangeStart,
        dateRangeEnd,
        page,
        pageSize,
      ];
}

/// Respuesta paginada de mantenimientos
class MaintenanceList extends Equatable {
  final List<Maintenance> items;
  final int totalItems;
  final int totalPages;
  final int currentPage;

  const MaintenanceList({
    required this.items,
    required this.totalItems,
    required this.totalPages,
    required this.currentPage,
  });

  @override
  List<Object?> get props => [items, totalItems, totalPages, currentPage];
}
