import '../entities/auth_entity.dart';
import '../failures/result.dart';

/// Puerto/Interfaz abstracta para autenticación
abstract class AuthRepository {
  /// Realiza login con credenciales
  Future<Result<AuthSession>> login(LoginCredentials credentials);

  /// Realiza logout
  Future<Result<void>> logout();

  /// Refresca el token usando refresh token
  Future<Result<AuthSession>> refreshToken(String refreshToken);

  /// Obtiene la sesión almacenada localmente
  Future<AuthSession?> getStoredSession();

  /// Guarda la sesión en almacenamiento seguro
  Future<Result<void>> saveSession(AuthSession session);

  /// Limpia la sesión del almacenamiento
  Future<Result<void>> clearSession();
}
