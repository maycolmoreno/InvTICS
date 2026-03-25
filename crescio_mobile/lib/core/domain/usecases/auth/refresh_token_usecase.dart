import '../../failures/result.dart';
import '../../entities/auth_entity.dart';
import '../../repositories/auth_repository.dart';

/// Use case para refrescar el token de autenticación
class RefreshTokenUseCase {
  final AuthRepository repository;

  RefreshTokenUseCase(this.repository);

  /// Ejecuta el refresh del token
  ///
  /// Parámetro: refreshToken - el refresh token almacenado
  /// Retorna: Result con la nueva sesión o Failure
  Future<Result<AuthSession>> call(String refreshToken) async {
    return await repository.refreshToken(refreshToken);
  }
}
