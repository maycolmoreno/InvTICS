import '../../failures/result.dart';
import '../../entities/maintenance_entity.dart';
import '../../repositories/maintenance_repository.dart';

/// Use case para crear un nuevo mantenimiento
class CreateMaintenanceUseCase {
  final MaintenanceRepository repository;

  CreateMaintenanceUseCase(this.repository);

  /// Crea un nuevo mantenimiento con los parámetros proporcionados
  Future<Result<Maintenance>> call(CreateMaintenanceParams params) async {
    return await repository.createMaintenance(params);
  }
}
