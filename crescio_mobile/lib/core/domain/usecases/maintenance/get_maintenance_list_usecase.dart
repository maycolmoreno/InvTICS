import '../../failures/result.dart';
import '../../entities/maintenance_entity.dart';
import '../../repositories/maintenance_repository.dart';

/// Use case para obtener lista de mantenimientos
class GetMaintenanceListUseCase {
  final MaintenanceRepository repository;

  GetMaintenanceListUseCase(this.repository);

  /// Ejecuta la búsqueda de mantenimientos con filtros
  Future<Result<MaintenanceList>> call(MaintenanceFilter filter) async {
    return await repository.getMaintenanceList(filter);
  }
}
