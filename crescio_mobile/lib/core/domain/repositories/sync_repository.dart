import '../entities/common_entity.dart';
import '../failures/result.dart';

/// Puerto/Interfaz abstracta para operaciones de sincronización
abstract class SyncRepository {
  /// Obtiene operaciones pendientes de sincronizar
  Future<Result<List<SyncOperation>>> getPendingOperations();

  /// Sincroniza todas las operaciones pendientes
  Future<Result<void>> syncPendingOperations();

  /// Marca una operación como procesada
  Future<Result<void>> markAsProcessed(String operationId);

  /// Marca una operación como fallida
  Future<Result<void>> markAsFailed(String operationId, String errorMessage);

  /// Limpia operaciones procesadas
  Future<Result<void>> clearProcessedOperations();

  /// Obtiene el estado de sincronización
  Future<Result<SyncStatus>> getSyncStatus();
}

/// Estado de la sincronización
class SyncStatus {
  final int pendingCount;
  final int failedCount;
  final DateTime? lastSyncTime;
  final bool isSyncing;

  const SyncStatus({
    required this.pendingCount,
    required this.failedCount,
    this.lastSyncTime,
    required this.isSyncing,
  });

  bool get hasPending => pendingCount > 0;
  bool get canSync => !isSyncing && hasPending;
}
