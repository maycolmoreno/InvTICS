import '../../failures/result.dart';
import '../../repositories/equipment_repository.dart';

/// Use case para sincronizar equipos desde el servidor
class SyncEquipmentUseCase {
  final EquipmentRepository repository;

  SyncEquipmentUseCase(this.repository);

  /// Sincroniza los equipos desde el servidor remoto
  Future<Result<void>> call() async {
    return await repository.syncEquipment();
  }
}
