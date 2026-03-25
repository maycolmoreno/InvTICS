import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:cresio_mobile/core/domain/usecases/auth/login_usecase.dart';
import 'package:cresio_mobile/core/domain/entities/auth_entity.dart';
import 'package:cresio_mobile/core/domain/failures/failures.dart';
import 'package:cresio_mobile/core/domain/failures/result.dart';
import '../../../../fixtures/factories/auth_session_factory.dart';
import '../../../../helpers/test_helpers.mocks.dart';

void main() {
  late MockAuthRepository mockRepository;
  late LoginUseCase useCase;

  setUp(() {
    mockRepository = MockAuthRepository();
    useCase = LoginUseCase(mockRepository);
  });

  group('LoginUseCase', () {
    test('Calls repository.login with provided credentials', () async {
      final credentials = AuthSessionFactory.createCredentials();
      final session = AuthSessionFactory.createSession();

      when(mockRepository.login(credentials))
          .thenAnswer((_) async => Success(session));

      await useCase.call(credentials);

      verify(mockRepository.login(credentials)).called(1);
    });

    test('Returns Success when login succeeds', () async {
      final credentials = AuthSessionFactory.createCredentials();
      final session = AuthSessionFactory.createSession();

      when(mockRepository.login(credentials))
          .thenAnswer((_) async => Success(session));

      final result = await useCase.call(credentials);

      expect(result.isSuccess, true);
      expect(result.getOrNull(), session);
    });

    test('Returns AuthFailure when credentials are invalid', () async {
      final credentials = AuthSessionFactory.createCredentials();
      final failure = AuthFailure('Invalid credentials');

      when(mockRepository.login(credentials))
          .thenAnswer((_) async => Failure(failure));

      final result = await useCase.call(credentials);

      expect(result.isFailure, true);
      expect(result.getFailureOrNull(), failure);
    });

    test('Returns ServerFailure when server error occurs', () async {
      final credentials = AuthSessionFactory.createCredentials();
      final failure = ServerFailure('Server error');

      when(mockRepository.login(credentials))
          .thenAnswer((_) async => Failure(failure));

      final result = await useCase.call(credentials);

      expect(result.isFailure, true);
      expect(result.getFailureOrNull(), failure);
    });

    test('Returns correct session data on successful login', () async {
      final credentials = LoginCredentials(
        username: 'admin',
        password: 'password123',
      );

      final session = AuthSessionFactory.createSession(
        username: 'admin',
        displayName: 'Admin User',
        role: 'ADMIN',
      );

      when(mockRepository.login(credentials))
          .thenAnswer((_) async => Success(session));

      final result = await useCase.call(credentials);

      final sessionData = result.getOrNull();
      expect(sessionData!.user.username, 'admin');
      expect(sessionData.user.displayName, 'Admin User');
      expect(sessionData.user.role, 'ADMIN');
    });

    test('Can be called multiple times with different credentials', () async {
      final cred1 = LoginCredentials(username: 'user1', password: 'pass1');
      final cred2 = LoginCredentials(username: 'user2', password: 'pass2');

      final session1 = AuthSessionFactory.createSession(username: 'user1');
      final session2 = AuthSessionFactory.createSession(username: 'user2');

      when(mockRepository.login(cred1))
          .thenAnswer((_) async => Success(session1));
      when(mockRepository.login(cred2))
          .thenAnswer((_) async => Success(session2));

      final result1 = await useCase.call(cred1);
      final result2 = await useCase.call(cred2);

      expect(result1.getOrNull()!.user.username, 'user1');
      expect(result2.getOrNull()!.user.username, 'user2');
      verify(mockRepository.login(cred1)).called(1);
      verify(mockRepository.login(cred2)).called(1);
    });

    test('Session contains valid token and refreshToken', () async {
      final credentials = AuthSessionFactory.createCredentials();
      final session = AuthSessionFactory.createSession(
        token: 'jwt-token-123',
        refreshToken: 'refresh-token-456',
      );

      when(mockRepository.login(credentials))
          .thenAnswer((_) async => Success(session));

      final result = await useCase.call(credentials);

      final sessionData = result.getOrNull()!;
      expect(sessionData.token, 'jwt-token-123');
      expect(sessionData.refreshToken, 'refresh-token-456');
      expect(sessionData.token.isNotEmpty, true);
      expect(sessionData.refreshToken.isNotEmpty, true);
    });

    test('Session expiration time is in the future', () async {
      final credentials = AuthSessionFactory.createCredentials();
      final session = AuthSessionFactory.createSession();

      when(mockRepository.login(credentials))
          .thenAnswer((_) async => Success(session));

      final result = await useCase.call(credentials);

      final sessionData = result.getOrNull()!;
      expect(sessionData.isExpired, false);
      expect(sessionData.expiresAt.isAfter(DateTime.now()), true);
    });
  });
}
