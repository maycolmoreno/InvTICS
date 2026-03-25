import '../../failures/result.dart';
import '../../repositories/sync_repository.dart';

/// Use case para sincronizar operaciones pendientes
class SyncPendingOperationsUseCase {
  final SyncRepository repository;

  SyncPendingOperationsUseCase(this.repository);

  /// Sincroniza todas las operaciones pendientes con el servidor
  Future<Result<void>> call() async {
    return await repository.syncPendingOperations();
  }
}
