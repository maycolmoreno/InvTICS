import '../entities/maintenance_entity.dart';
import '../failures/result.dart';

/// Puerto/Interfaz abstracta para operaciones de mantenimientos
abstract class MaintenanceRepository {
  /// Obtiene la lista de mantenimientos con filtros
  Future<Result<MaintenanceList>> getMaintenanceList(MaintenanceFilter filter);

  /// Obtiene un mantenimiento por ID
  Future<Result<Maintenance>> getMaintenanceDetail(String id);

  /// Crea un nuevo mantenimiento
  Future<Result<Maintenance>> createMaintenance(CreateMaintenanceParams params);

  /// Actualiza un mantenimiento
  Future<Result<Maintenance>> updateMaintenance(
    String id,
    CreateMaintenanceParams params,
  );

  /// Obtiene mantenimientos locales almacenados
  Future<Result<List<Maintenance>>> getLocalMaintenance();

  /// Sincroniza mantenimientos del servidor
  Future<Result<void>> syncMaintenance();

  /// Guarda mantenimientos en almacenamiento local
  Future<Result<void>> saveLocalMaintenance(List<Maintenance> maintenance);
}
