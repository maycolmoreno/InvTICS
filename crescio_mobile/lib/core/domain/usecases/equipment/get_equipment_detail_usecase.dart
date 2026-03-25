import '../../failures/result.dart';
import '../../entities/equipment_entity.dart';
import '../../repositories/equipment_repository.dart';

/// Use case para obtener detalles de un equipo
class GetEquipmentDetailUseCase {
  final EquipmentRepository repository;

  GetEquipmentDetailUseCase(this.repository);

  /// Obtiene los detalles de un equipo por ID
  Future<Result<Equipment>> call(String equipmentId) async {
    return await repository.getEquipmentDetail(equipmentId);
  }
}
