import '../../core/storage/local_database.dart';

class SyncService {
  Future<int> syncPendingMantenimientos() async {
    final pending = await LocalDatabase.instance.listarPendientes();
    if (pending.isEmpty) {
      return 0;
    }

    // Implementacion pendiente:
    // 1. Crear mantenimiento en API
    // 2. Subir fotos y firmas
    // 3. Cerrar mantenimiento
    // 4. Marcar sincronizado
    return 0;
  }
}
