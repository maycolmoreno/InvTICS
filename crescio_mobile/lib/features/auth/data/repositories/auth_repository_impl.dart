import '../../../core/domain/failures/failures.dart';
import '../../../core/domain/failures/result.dart';
import '../../../core/domain/entities/auth_entity.dart';
import '../../../core/domain/repositories/auth_repository.dart' as domain;
import '../datasources/auth_local_datasource.dart';
import '../datasources/auth_remote_datasource.dart';
import '../models/auth_session_model.dart';

/// Implementación de AuthRepository que coordina local y remote datasources
class AuthRepositoryImpl implements domain.AuthRepository {
  final AuthRemoteDatasource remoteDatasource;
  final AuthLocalDatasource localDatasource;

  AuthRepositoryImpl({
    required this.remoteDatasource,
    required this.localDatasource,
  });

  @override
  Future<Result<AuthSession>> login(LoginCredentials credentials) async {
    try {
      final session = await remoteDatasource.login(
        credentials.username,
        credentials.password,
      );

      // Guardar en local
      await localDatasource.saveSession(session);

      return Success(session);
    } on AuthException catch (e) {
      return Failure(AuthFailure(e.message));
    } on ServerException catch (e) {
      return Failure(ServerFailure(e.message));
    } catch (e) {
      return Failure(UnexpectedFailure(e.toString()));
    }
  }

  @override
  Future<Result<void>> logout() async {
    try {
      await localDatasource.clearSession();
      return Success(null);
    } on StorageFailure catch (e) {
      return Failure(e);
    } catch (e) {
      return Failure(StorageFailure(e.toString()));
    }
  }

  @override
  Future<Result<AuthSession>> refreshToken(String refreshToken) async {
    try {
      final session = await remoteDatasource.refreshToken(refreshToken);
      await localDatasource.saveSession(session);
      return Success(session);
    } on SessionExpiredFailure catch (e) {
      return Failure(e);
    } on ServerException catch (e) {
      return Failure(ServerFailure(e.message));
    } catch (e) {
      return Failure(SessionExpiredFailure(e.toString()));
    }
  }

  @override
  Future<AuthSession?> getStoredSession() async {
    try {
      return await localDatasource.readStoredSession();
    } catch (_) {
      return null;
    }
  }

  @override
  Future<Result<void>> saveSession(AuthSession session) async {
    try {
      if (session is AuthSessionModel) {
        await localDatasource.saveSession(session);
      } else {
        // Convertir a modelo
        await localDatasource.saveSession(
          AuthSessionModel(
            token: session.token,
            refreshToken: session.refreshToken,
            user: session.user,
            expiresAt: session.expiresAt,
            createdAt: session.createdAt,
          ),
        );
      }
      return Success(null);
    } on StorageFailure catch (e) {
      return Failure(e);
    } catch (e) {
      return Failure(StorageFailure(e.toString()));
    }
  }

  @override
  Future<Result<void>> clearSession() async {
    try {
      await localDatasource.clearSession();
      return Success(null);
    } on StorageFailure catch (e) {
      return Failure(e);
    } catch (e) {
      return Failure(StorageFailure(e.toString()));
    }
  }
}
