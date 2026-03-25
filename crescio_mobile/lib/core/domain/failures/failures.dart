/// Base class para todos los failures en la aplicacion
abstract class Failure {
  final String message;

  const Failure(this.message);

  @override
  String toString() => message;
}

/// Fallo en la autenticacion
class AuthFailure extends Failure {
  const AuthFailure(String message) : super(message);
}

/// Fallo en autorizacion (permisos insuficientes)
class AuthorizationFailure extends Failure {
  const AuthorizationFailure(String message) : super(message);
}

/// Fallo de servidor remoto
class ServerFailure extends Failure {
  const ServerFailure(String message) : super(message);
}

/// Fallo de conectividad
class NetworkFailure extends Failure {
  const NetworkFailure(String message) : super(message);
}

/// Fallo no encontrado (404)
class NotFoundFailure extends Failure {
  const NotFoundFailure(String message) : super(message);
}

/// Fallo de validacion
class ValidationFailure extends Failure {
  const ValidationFailure(String message) : super(message);
}

/// Fallo de almacenamiento local
class StorageFailure extends Failure {
  const StorageFailure(String message) : super(message);
}

/// Fallo generico/desconocido
class UnexpectedFailure extends Failure {
  const UnexpectedFailure(String message) : super(message);
}

/// Fallo de sesion expirada
class SessionExpiredFailure extends Failure {
  const SessionExpiredFailure(String message) : super(message);
}
