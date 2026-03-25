import '../../failures/result.dart';
import '../../entities/maintenance_entity.dart';
import '../../repositories/maintenance_repository.dart';

/// Use case para actualizar un mantenimiento existente
class UpdateMaintenanceUseCase {
  final MaintenanceRepository repository;

  UpdateMaintenanceUseCase(this.repository);

  /// Actualiza un mantenimiento existente
  Future<Result<Maintenance>> call(
    String maintenanceId,
    CreateMaintenanceParams params,
  ) async {
    return await repository.updateMaintenance(maintenanceId, params);
  }
}
