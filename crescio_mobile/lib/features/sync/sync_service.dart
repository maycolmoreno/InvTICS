import '../../core/errors/exceptions.dart';
import '../../core/network/api_client.dart';
import '../../core/storage/local_database.dart';
import '../mantenimientos/data/mantenimientos_repository.dart';

class SyncService {
  SyncService(this._apiClient);

  final ApiClient _apiClient;

  Future<int> syncPendingOperations() async {
    final pending = await LocalDatabase.instance.listarSyncPendientes();
    if (pending.isEmpty) {
      return 0;
    }

    final repository = MantenimientosRepository(_apiClient);
    var synced = 0;

    for (final item in pending) {
      final id = item['id']?.toString() ?? '';
      final operation = item['operation']?.toString() ?? '';
      final payload = Map<String, dynamic>.from(item['payload'] as Map? ?? const {});

      try {
        switch (operation) {
          case 'create_mantenimiento':
            await repository.syncQueuedCreate(payload);
            break;
          case 'close_mantenimiento':
            await repository.syncQueuedClose(payload);
            break;
          default:
            await LocalDatabase.instance.registrarSyncError(
              id,
              'Operacion no soportada: $operation',
            );
            continue;
        }
        await LocalDatabase.instance.marcarSyncCompletado(id);
        synced++;
      } on OfflineException {
        break;
      } catch (error) {
        await LocalDatabase.instance.registrarSyncError(id, error.toString());
      }
    }

    return synced;
  }
}
