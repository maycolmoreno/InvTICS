import '../../failures/result.dart';
import '../../repositories/auth_repository.dart';

/// Use case para logout del usuario
class LogoutUseCase {
  final AuthRepository repository;

  LogoutUseCase(this.repository);

  /// Ejecuta el logout
  Future<Result<void>> call() async {
    return await repository.logout();
  }
}
