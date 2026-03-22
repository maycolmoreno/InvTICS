class OfflineException implements Exception {
  final String message;
  const OfflineException([this.message = 'Sin conexion con el servidor.']);
}

class ServerException implements Exception {
  final String message;
  const ServerException([this.message = 'Ocurrio un error en el servidor.']);
}

class NotFoundException implements Exception {
  final String message;
  const NotFoundException([this.message = 'No se encontro la informacion solicitada.']);
}

class AuthException implements Exception {
  final String message;
  const AuthException([this.message = 'Tu sesion ya no es valida.']);
}
