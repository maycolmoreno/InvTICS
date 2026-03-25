import 'package:cresio_mobile/core/domain/entities/equipment_entity.dart';

/// Factory para crear instancias de prueba de Equipment
class EquipmentFactory {
  static Equipment createEquipment({
    String id = 'eq-123',
    String codigo = 'EQ-001',
    String nombre = 'Laptop Dell',
    String? descripcion,
    String? modelo = 'XPS 13',
    String? serie = 'SN-12345',
    String? marca = 'Dell',
    EquipmentStatus estado = EquipmentStatus.activo,
    String? ubicacion = 'Oficina Piso 2',
    String? custodio = 'Juan Pérez',
    DateTime? fechaAdquisicion,
    String? caracteristicas,
    double? latitud = 0.0,
    double? longitud = 0.0,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    final now = DateTime.now();
    return Equipment(
      id: id,
      codigo: codigo,
      nombre: nombre,
      descripcion: descripcion,
      modelo: modelo,
      serie: serie,
      marca: marca,
      estado: estado,
      ubicacion: ubicacion,
      custodio: custodio,
      fechaAdquisicion: fechaAdquisicion,
      caracteristicas: caracteristicas,
      latitud: latitud,
      longitud: longitud,
      createdAt: createdAt ?? now,
      updatedAt: updatedAt,
    );
  }

  static Equipment createInactiveEquipment() {
    return createEquipment(
      id: 'eq-456',
      codigo: 'EQ-002',
      nombre: 'Monitor Samsung',
      estado: EquipmentStatus.inactivo,
    );
  }

  static Equipment createMaintenanceEquipment() {
    return createEquipment(
      id: 'eq-789',
      codigo: 'EQ-003',
      nombre: 'Impresora HP',
      estado: EquipmentStatus.mantenimiento,
    );
  }

  static EquipmentFilter createFilter({
    String? search,
    EquipmentStatus? estado,
    String? ubicacion,
    String? custodio,
    int page = 1,
    int pageSize = 10,
  }) {
    return EquipmentFilter(
      search: search,
      estado: estado,
      ubicacion: ubicacion,
      custodio: custodio,
      page: page,
      pageSize: pageSize,
    );
  }
}
