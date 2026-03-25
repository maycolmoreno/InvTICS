import '../../entities/auth_entity.dart';
import '../../repositories/auth_repository.dart';

/// Use case para obtener la sesión almacenada
class GetStoredSessionUseCase {
  final AuthRepository repository;

  GetStoredSessionUseCase(this.repository);

  /// Obtiene la sesión guardada en almacenamiento seguro
  /// Retorna la sesión o null si no existe
  Future<AuthSession?> call() async {
    return await repository.getStoredSession();
  }
}
