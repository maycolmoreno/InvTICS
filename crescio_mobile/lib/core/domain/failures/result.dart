import 'failures.dart';

/// Either es un patrón funcional que representa un resultado que puede ser:
/// - Success(data) - éxito con valor
/// - Failure(error) - error con failure
///
/// Ejemplo:
/// Either<Failure, String> result = await _repository.fetchData();
/// result.fold(
///   (failure) => print('Error: ${failure.message}'),
///   (data) => print('Éxito: $data'),
/// );
abstract class Either<L, R> {
  /// Mapea ambos tipos usando callbacks
  X fold<X>(
    X Function(L) onLeft,
    X Function(R) onRight,
  );

  /// Mapea el lado derecho (success)
  Either<L, X> map<X>(X Function(R) f);

  /// Mapea el lado izquierdo (failure)
  Either<X, R> mapLeft<X>(X Function(L) f);

  /// Retorna true si es un success
  bool get isSuccess;

  /// Retorna true si es un failure
  bool get isFailure;

  /// Obtiene el valor (si es success)
  R? getOrNull();

  /// Obtiene el failure (si es failure)
  L? getFailureOrNull();
}

/// Success - representa un resultado exitoso
class Success<L, R> extends Either<L, R> {
  final R value;

  Success(this.value);

  @override
  X fold<X>(X Function(L) onLeft, X Function(R) onRight) => onRight(value);

  @override
  Either<L, X> map<X>(X Function(R) f) => Success(f(value));

  @override
  Either<X, R> mapLeft<X>(X Function(L) f) => Success(value);

  @override
  bool get isSuccess => true;

  @override
  bool get isFailure => false;

  @override
  R? getOrNull() => value;

  @override
  L? getFailureOrNull() => null;

  @override
  String toString() => 'Success($value)';
}

/// Failure - representa un resultado fallido
class Failure<L, R> extends Either<L, R> {
  final L failure;

  Failure(this.failure);

  @override
  X fold<X>(X Function(L) onLeft, X Function(R) onRight) => onLeft(failure);

  @override
  Either<L, X> map<X>(X Function(R) f) => Failure(failure);

  @override
  Either<X, R> mapLeft<X>(X Function(L) f) => Failure(f(failure));

  @override
  bool get isSuccess => false;

  @override
  bool get isFailure => true;

  @override
  R? getOrNull() => null;

  @override
  L? getFailureOrNull() => failure;

  @override
  String toString() => 'Failure($failure)';
}

/// Type alias para simplificar tipado: Result<Failure, Data>
typedef Result<R> = Either<failures.Failure, R>;
