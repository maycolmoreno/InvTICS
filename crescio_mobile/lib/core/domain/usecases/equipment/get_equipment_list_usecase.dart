import '../../failures/result.dart';
import '../../entities/equipment_entity.dart';
import '../../repositories/equipment_repository.dart';

/// Use case para obtener lista de equipos
class GetEquipmentListUseCase {
  final EquipmentRepository repository;

  GetEquipmentListUseCase(this.repository);

  /// Ejecuta la búsqueda de equipos con filtros
  Future<Result<EquipmentList>> call(EquipmentFilter filter) async {
    return await repository.getEquipmentList(filter);
  }
}
