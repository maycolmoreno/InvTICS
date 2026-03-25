import '../entities/equipment_entity.dart';
import '../failures/result.dart';

/// Puerto/Interfaz abstracta para operaciones de equipos
abstract class EquipmentRepository {
  /// Obtiene la lista de equipos con filtros
  Future<Result<EquipmentList>> getEquipmentList(EquipmentFilter filter);

  /// Obtiene un equipo por ID
  Future<Result<Equipment>> getEquipmentDetail(String id);

  /// Obtiene equipos locales almacenados
  Future<Result<List<Equipment>>> getLocalEquipment();

  /// Sincroniza equipos del servidor
  Future<Result<void>> syncEquipment();

  /// Guarda equipos en almacenamiento local
  Future<Result<void>> saveLocalEquipment(List<Equipment> equipment);
}
