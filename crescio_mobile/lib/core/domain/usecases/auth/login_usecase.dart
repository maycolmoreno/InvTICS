import '../../failures/result.dart';
import '../../entities/auth_entity.dart';
import '../../repositories/auth_repository.dart';

/// Use case para login de usuario
class LoginUseCase {
  final AuthRepository repository;

  LoginUseCase(this.repository);

  /// Ejecuta el login con las credenciales proporcionadas
  Future<Result<AuthSession>> call(LoginCredentials credentials) async {
    return await repository.login(credentials);
  }
}
