import 'package:cresio_mobile/core/domain/entities/maintenance_entity.dart';

/// Factory para crear instancias de prueba de Maintenance
class MaintenanceFactory {
  static Maintenance createMaintenance({
    String id = 'maint-123',
    String codigo = 'MAINT-001',
    String equipoId = 'eq-123',
    String? equipoCodigo = 'EQ-001',
    String? equipoNombre = 'Laptop Dell',
    MaintenanceType tipo = MaintenanceType.preventivo,
    MaintenanceStatus estado = MaintenanceStatus.pendiente,
    String descripcion = 'Limpieza y actualización de software',
    String? observaciones,
    String? tecnicoAsignado = 'tech-123',
    String? tecnicoNombre = 'Carlos López',
    DateTime? fechaProgramada,
    DateTime? fechaInicio,
    DateTime? fechaFin,
    double? costo,
    List<String>? partesUtilizadas,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    final now = DateTime.now();
    return Maintenance(
      id: id,
      codigo: codigo,
      equipoId: equipoId,
      equipoCodigo: equipoCodigo,
      equipoNombre: equipoNombre,
      tipo: tipo,
      estado: estado,
      descripcion: descripcion,
      observaciones: observaciones,
      tecnicoAsignado: tecnicoAsignado,
      tecnicoNombre: tecnicoNombre,
      fechaProgamada: fechaProgramada,
      fechaInicio: fechaInicio,
      fechaFin: fechaFin,
      costo: costo,
      partesUtilizadas: partesUtilizadas,
      createdAt: createdAt ?? now,
      updatedAt: updatedAt,
    );
  }

  static Maintenance createInProgressMaintenance() {
    final now = DateTime.now();
    return createMaintenance(
      id: 'maint-456',
      codigo: 'MAINT-002',
      estado: MaintenanceStatus.enProgreso,
      fechaInicio: now.subtract(const Duration(hours: 2)),
    );
  }

  static Maintenance createCompletedMaintenance() {
    final now = DateTime.now();
    return createMaintenance(
      id: 'maint-789',
      codigo: 'MAINT-003',
      estado: MaintenanceStatus.completado,
      fechaInicio: now.subtract(const Duration(days: 1)),
      fechaFin: now.subtract(const Duration(hours: 12)),
      costo: 150.0,
    );
  }

  static Maintenance createCorrectiveMaintenance() {
    return createMaintenance(
      id: 'maint-999',
      codigo: 'MAINT-004',
      tipo: MaintenanceType.correctivo,
      descripcion: 'Reparación de pantalla dañada',
      estado: MaintenanceStatus.enProgreso,
    );
  }

  static CreateMaintenanceParams createParams({
    String equipoId = 'eq-123',
    MaintenanceType tipo = MaintenanceType.preventivo,
    String descripcion = 'Mantenimiento preventivo',
    String? observaciones,
    DateTime? fechaProgramada,
  }) {
    return CreateMaintenanceParams(
      equipoId: equipoId,
      tipo: tipo,
      descripcion: descripcion,
      observaciones: observaciones,
      fechaProgramada: fechaProgramada,
    );
  }
}
