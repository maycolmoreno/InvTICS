import 'package:flutter_test/flutter_test.dart';
import 'package:cresio_mobile/core/domain/entities/equipment_entity.dart';

void main() {
  group('Equipment Entity', () {
    final now = DateTime.now();

    test('Creates Equipment with correct properties', () {
      final equipment = Equipment(
        id: 'eq-1',
        codigo: 'EQ-001',
        nombre: 'Laptop Dell',
        descripcion: 'Equipo de trabajo',
        modelo: 'XPS 13',
        serie: 'SN-12345',
        marca: 'Dell',
        estado: EquipmentStatus.activo,
        ubicacion: 'Oficina Piso 2',
        custodio: 'Juan Pérez',
        fechaAdquisicion: now.subtract(const Duration(days: 365)),
        caracteristicas: 'RAM: 16GB, SSD: 512GB',
        latitud: 0.123456,
        longitud: -0.654321,
        createdAt: now,
      );

      expect(equipment.id, 'eq-1');
      expect(equipment.codigo, 'EQ-001');
      expect(equipment.nombre, 'Laptop Dell');
      expect(equipment.estado, EquipmentStatus.activo);
      expect(equipment.createdAt, now);
      expect(equipment.latitud, 0.123456);
    });

    test('Compares two identical Equipment instances correctly', () {
      final eq1 = Equipment(
        id: 'eq-1',
        codigo: 'EQ-001',
        nombre: 'Laptop Dell',
        estado: EquipmentStatus.activo,
        createdAt: now,
      );

      final eq2 = Equipment(
        id: 'eq-1',
        codigo: 'EQ-001',
        nombre: 'Laptop Dell',
        estado: EquipmentStatus.activo,
        createdAt: now,
      );

      expect(eq1, equals(eq2));
    });

    test('Equipment with different properties are not equal', () {
      final eq1 = Equipment(
        id: 'eq-1',
        codigo: 'EQ-001',
        nombre: 'Laptop Dell',
        estado: EquipmentStatus.activo,
        createdAt: now,
      );

      final eq2 = Equipment(
        id: 'eq-2',
        codigo: 'EQ-002',
        nombre: 'Monitor Samsung',
        estado: EquipmentStatus.inactivo,
        createdAt: now,
      );

      expect(eq1, isNot(equals(eq2)));
    });

    test('Equipment with null optional fields', () {
      final equipment = Equipment(
        id: 'eq-1',
        codigo: 'EQ-001',
        nombre: 'Equipo Genérico',
        estado: EquipmentStatus.activo,
        createdAt: now,
      );

      expect(equipment.descripcion, null);
      expect(equipment.modelo, null);
      expect(equipment.serie, null);
      expect(equipment.ubicacion, null);
      expect(equipment.updatedAt, null);
    });
  });

  group('EquipmentStatus Enum', () {
    test('Has correct enum values', () {
      expect(EquipmentStatus.activo, EquipmentStatus.activo);
      expect(EquipmentStatus.inactivo, EquipmentStatus.inactivo);
      expect(EquipmentStatus.mantenimiento, EquipmentStatus.mantenimiento);
      expect(EquipmentStatus.desechado, EquipmentStatus.desechado);
    });

    test('Can compare enum values', () {
      expect(
        EquipmentStatus.activo != EquipmentStatus.inactivo,
        true,
      );
    });
  });

  group('EquipmentFilter', () {
    test('Creates filter with default pagination', () {
      final filter = EquipmentFilter(
        search: 'laptop',
        estado: EquipmentStatus.activo,
      );

      expect(filter.search, 'laptop');
      expect(filter.estado, EquipmentStatus.activo);
      expect(filter.page, 1);
      expect(filter.pageSize, 10);
    });

    test('Creates filter with custom pagination', () {
      final filter = EquipmentFilter(
        search: 'monitor',
        page: 2,
        pageSize: 25,
      );

      expect(filter.page, 2);
      expect(filter.pageSize, 25);
    });

    test('Compares two identical filters correctly', () {
      const filter1 = EquipmentFilter(
        search: 'laptop',
        estado: EquipmentStatus.activo,
      );

      const filter2 = EquipmentFilter(
        search: 'laptop',
        estado: EquipmentStatus.activo,
      );

      expect(filter1, equals(filter2));
    });
  });
}
