import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:cresio_mobile/features/auth/data/repositories/auth_repository_impl.dart';
import 'package:cresio_mobile/features/auth/data/models/auth_session_model.dart';
import 'package:cresio_mobile/core/domain/entities/auth_entity.dart';
import 'package:cresio_mobile/core/domain/failures/failures.dart';
import 'package:cresio_mobile/core/domain/failures/result.dart';
import '../../../../fixtures/factories/auth_session_factory.dart';
import '../../../../helpers/test_helpers.mocks.dart';

void main() {
  late MockAuthRemoteDatasource mockRemoteDatasource;
  late MockAuthLocalDatasource mockLocalDatasource;
  late AuthRepositoryImpl repository;

  setUp(() {
    mockRemoteDatasource = MockAuthRemoteDatasource();
    mockLocalDatasource = MockAuthLocalDatasource();
    repository = AuthRepositoryImpl(
      remoteDatasource: mockRemoteDatasource,
      localDatasource: mockLocalDatasource,
    );
  });

  group('AuthRepositoryImpl', () {
    group('login', () {
      test('login returns Success when credentials are valid', () async {
        final credentials = AuthSessionFactory.createCredentials();
        final session = AuthSessionFactory.createSession();
        final sessionModel = AuthSessionModel(
          token: session.token,
          refreshToken: session.refreshToken,
          user: session.user,
          expiresAt: session.expiresAt,
          createdAt: session.createdAt,
        );

        when(mockRemoteDatasource.login(
          credentials.username,
          credentials.password,
        )).thenAnswer((_) async => sessionModel);

        when(mockLocalDatasource.saveSession(sessionModel))
            .thenAnswer((_) async => {});

        final result = await repository.login(credentials);

        expect(result.isSuccess, true);
        expect(result.getOrNull(), isNotNull);
        verify(mockRemoteDatasource.login(
          credentials.username,
          credentials.password,
        )).called(1);
        verify(mockLocalDatasource.saveSession(sessionModel)).called(1);
      });

      test('login returns AuthFailure on AuthException', () async {
        final credentials = AuthSessionFactory.createCredentials();

        when(mockRemoteDatasource.login(
          credentials.username,
          credentials.password,
        )).thenThrow(AuthException('Invalid credentials'));

        final result = await repository.login(credentials);

        expect(result.isFailure, true);
        expect(result.getFailureOrNull(), isA<AuthFailure>());
      });

      test('login returns ServerFailure on ServerException', () async {
        final credentials = AuthSessionFactory.createCredentials();

        when(mockRemoteDatasource.login(
          credentials.username,
          credentials.password,
        )).thenThrow(ServerException('Server error'));

        final result = await repository.login(credentials);

        expect(result.isFailure, true);
        expect(result.getFailureOrNull(), isA<ServerFailure>());
      });

      test('login returns UnexpectedFailure on unknown exception', () async {
        final credentials = AuthSessionFactory.createCredentials();

        when(mockRemoteDatasource.login(
          credentials.username,
          credentials.password,
        )).thenThrow(Exception('Unknown error'));

        final result = await repository.login(credentials);

        expect(result.isFailure, true);
        expect(result.getFailureOrNull(), isA<UnexpectedFailure>());
      });

      test('login saves session to local storage', () async {
        final credentials = AuthSessionFactory.createCredentials();
        final session = AuthSessionFactory.createSession();
        final sessionModel = AuthSessionModel(
          token: session.token,
          refreshToken: session.refreshToken,
          user: session.user,
          expiresAt: session.expiresAt,
          createdAt: session.createdAt,
        );

        when(mockRemoteDatasource.login(
          credentials.username,
          credentials.password,
        )).thenAnswer((_) async => sessionModel);

        when(mockLocalDatasource.saveSession(any))
            .thenAnswer((_) async => {});

        await repository.login(credentials);

        verify(mockLocalDatasource.saveSession(any)).called(1);
      });
    });

    group('logout', () {
      test('logout clears local storage and returns Success', () async {
        when(mockLocalDatasource.clearSession())
            .thenAnswer((_) async => {});

        final result = await repository.logout();

        expect(result.isSuccess, true);
        verify(mockLocalDatasource.clearSession()).called(1);
      });

      test('logout returns StorageFailure on error', () async {
        when(mockLocalDatasource.clearSession())
            .thenThrow(Exception('Storage error'));

        final result = await repository.logout();

        expect(result.isFailure, true);
        expect(result.getFailureOrNull(), isA<StorageFailure>());
      });
    });

    group('refreshToken', () {
      test('refreshToken returns Success when refresh succeeds', () async {
        final refreshToken = 'refresh-token-123';
        final session = AuthSessionFactory.createSession();
        final sessionModel = AuthSessionModel(
          token: session.token,
          refreshToken: session.refreshToken,
          user: session.user,
          expiresAt: session.expiresAt,
          createdAt: session.createdAt,
        );

        when(mockRemoteDatasource.refreshToken(refreshToken))
            .thenAnswer((_) async => sessionModel);

        when(mockLocalDatasource.saveSession(any))
            .thenAnswer((_) async => {});

        final result = await repository.refreshToken(refreshToken);

        expect(result.isSuccess, true);
        verify(mockRemoteDatasource.refreshToken(refreshToken)).called(1);
      });

      test('refreshToken returns SessionExpiredFailure on error', () async {
        final refreshToken = 'invalid-refresh-token';

        when(mockRemoteDatasource.refreshToken(refreshToken))
            .thenThrow(Exception('Session expired'));

        final result = await repository.refreshToken(refreshToken);

        expect(result.isFailure, true);
        expect(result.getFailureOrNull(), isA<SessionExpiredFailure>());
      });
    });

    group('getStoredSession', () {
      test('getStoredSession returns stored session when available', () async {
        final session = AuthSessionFactory.createSession();
        final sessionModel = AuthSessionModel(
          token: session.token,
          refreshToken: session.refreshToken,
          user: session.user,
          expiresAt: session.expiresAt,
          createdAt: session.createdAt,
        );

        when(mockLocalDatasource.readStoredSession())
            .thenAnswer((_) async => sessionModel);

        final result = await repository.getStoredSession();

        expect(result, isNotNull);
        expect(result!.token, session.token);
      });

      test('getStoredSession returns null when no session stored', () async {
        when(mockLocalDatasource.readStoredSession())
            .thenThrow(Exception('No session'));

        final result = await repository.getStoredSession();

        expect(result, null);
      });
    });

    group('saveSession', () {
      test('saveSession saves AuthSessionModel successfully', () async {
        final session = AuthSessionFactory.createSession();
        final sessionModel = AuthSessionModel(
          token: session.token,
          refreshToken: session.refreshToken,
          user: session.user,
          expiresAt: session.expiresAt,
          createdAt: session.createdAt,
        );

        when(mockLocalDatasource.saveSession(any))
            .thenAnswer((_) async => {});

        final result = await repository.saveSession(sessionModel);

        expect(result.isSuccess, true);
        verify(mockLocalDatasource.saveSession(any)).called(1);
      });

      test('saveSession returns StorageFailure on error', () async {
        final session = AuthSessionFactory.createSession();

        when(mockLocalDatasource.saveSession(any))
            .thenThrow(Exception('Storage error'));

        final result = await repository.saveSession(session);

        expect(result.isFailure, true);
        expect(result.getFailureOrNull(), isA<StorageFailure>());
      });
    });
  });
}
