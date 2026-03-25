import 'package:flutter_test/flutter_test.dart';
import 'package:cresio_mobile/core/domain/failures/result.dart';
import 'package:cresio_mobile/core/domain/failures/failures.dart';

void main() {
  group('Either<Failure, Success> Pattern - Success', () {
    test('Success.fold returns right callback result', () {
      final result = Success<AppFailure, String>('Hello World');

      final value = result.fold(
        (failure) => 'error',
        (success) => success,
      );

      expect(value, 'Hello World');
    });

    test('Success.map transforms the value', () {
      final result = Success<AppFailure, int>(42);
      final mapped = result.map((value) => value * 2);

      final value = mapped.fold(
        (failure) => 0,
        (success) => success,
      );

      expect(value, 84);
    });

    test('Success.isSuccess returns true', () {
      final result = Success<AppFailure, String>('success');
      expect(result.isSuccess, true);
    });

    test('Success.isFailure returns false', () {
      final result = Success<AppFailure, String>('success');
      expect(result.isFailure, false);
    });

    test('Success.getOrNull returns value', () {
      final result = Success<AppFailure, String>('value');
      expect(result.getOrNull(), 'value');
    });

    test('Success.getFailureOrNull returns null', () {
      final result = Success<AppFailure, String>('value');
      expect(result.getFailureOrNull(), null);
    });

    test('Success.mapLeft does not apply transformation', () {
      final result = Success<AppFailure, String>('value');
      final mapped = result.mapLeft((failure) => ServerFailure('error'));

      expect(mapped.isSuccess, true);
      expect(mapped.getOrNull(), 'value');
    });

    test('Success with null value', () {
      final result = Success<AppFailure, String?>('');
      expect(result.getOrNull(), '');
    });
  });

  group('Either<Failure, Success> Pattern - Failure', () {
    test('Failure.fold returns left callback result', () {
      final failure = AuthFailure('Invalid credentials');
      final result = Failure<AppFailure, String>(failure);

      final value = result.fold(
        (failure) => 'error: ${failure.message}',
        (success) => 'ok',
      );

      expect(value, 'error: Invalid credentials');
    });

    test('Failure.map does not apply transformation', () {
      final failure = AuthFailure('error');
      final result = Failure<AppFailure, String>(failure);
      final mapped = result.map((value) => value.toUpperCase());

      expect(mapped.isFailure, true);
      expect(mapped.getFailureOrNull(), failure);
    });

    test('Failure.mapLeft transforms the failure', () {
      final authFailure = AuthFailure('Invalid credentials');
      final result = Failure<AppFailure, String>(authFailure);

      final mapped = result.mapLeft((failure) {
        if (failure is AuthFailure) {
          return ServerFailure('Auth error: ${failure.message}');
        }
        return failure;
      });

      final newFailure = mapped.getFailureOrNull();
      expect(newFailure, isA<ServerFailure>());
    });

    test('Failure.isFailure returns true', () {
      final result = Failure<AppFailure, String>(AuthFailure('error'));
      expect(result.isFailure, true);
    });

    test('Failure.isSuccess returns false', () {
      final result = Failure<AppFailure, String>(AuthFailure('error'));
      expect(result.isSuccess, false);
    });

    test('Failure.getOrNull returns null', () {
      final result = Failure<AppFailure, String>(ServerFailure('error'));
      expect(result.getOrNull(), null);
    });

    test('Failure.getFailureOrNull returns failure', () {
      final failure = NetworkFailure('No internet');
      final result = Failure<AppFailure, String>(failure);
      expect(result.getFailureOrNull(), failure);
    });

    test('Failure containing different failure types', () {
      final authFailure = AuthFailure('Auth error');
      final result = Failure<AppFailure, String>(authFailure);

      expect(result.getFailureOrNull(), isA<AuthFailure>());
      expect(
        (result.getFailureOrNull() as AuthFailure).message,
        'Auth error',
      );
    });
  });

  group('Either<Failure, Success> Pattern - Integration', () {
    test('Chain multiple operations with Success', () {
      final result = Success<AppFailure, int>(10)
          .map((value) => value * 2)
          .map((value) => value + 5);

      final value = result.fold((_) => 0, (v) => v);
      expect(value, 25);
    });

    test('Chain stops at first Failure', () {
      final result = Failure<AppFailure, int>(ServerFailure('error'))
          .map((value) => value * 2)
          .map((value) => value + 5);

      expect(result.isFailure, true);
      expect(result.getOrNull(), null);
    });

    test('Can switch between Success and Failure with mapLeft', () {
      final initial = Failure<AppFailure, String>(
        NetworkFailure('No internet'),
      );

      final recovered = initial.mapLeft((failure) {
        if (failure is NetworkFailure) {
          return AuthFailure('Fallback error');
        }
        return failure;
      });

      expect(recovered.isFailure, true);
      expect(recovered.getFailureOrNull(), isA<AuthFailure>());
    });

    test('Success and Failure toString representation', () {
      final success = Success<AppFailure, String>('test');
      final failure = Failure<AppFailure, String>(ServerFailure('error'));

      expect(success.toString(), 'Success(test)');
      expect(failure.toString(), contains('Failure'));
    });
  });
}
