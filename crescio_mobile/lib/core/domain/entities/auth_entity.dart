import 'package:equatable/equatable.dart';

/// Usuario de auth - entidad de dominio pura
class AuthUser extends Equatable {
  final String id;
  final String username;
  final String displayName;
  final String email;
  final String role;
  final List<String> permissions;
  final DateTime? createdAt;
  final bool isActive;

  const AuthUser({
    required this.id,
    required this.username,
    required this.displayName,
    required this.email,
    required this.role,
    required this.permissions,
    this.createdAt,
    this.isActive = true,
  });

  @override
  List<Object?> get props => [
        id,
        username,
        displayName,
        email,
        role,
        permissions,
        createdAt,
        isActive,
      ];
}

/// Sesion de autenticacion - entidad de dominio
class AuthSession extends Equatable {
  final String token;
  final String refreshToken;
  final AuthUser user;
  final DateTime expiresAt;
  final DateTime createdAt;

  const AuthSession({
    required this.token,
    required this.refreshToken,
    required this.user,
    required this.expiresAt,
    required this.createdAt,
  });

  /// Retorna true si el token ha expirado
  bool get isExpired => DateTime.now().isAfter(expiresAt);

  /// Retorna true si está cerca de expirar (menos de 5 minutos)
  bool get isExpiringSoon {
    final remainingTime = expiresAt.difference(DateTime.now());
    return remainingTime.inMinutes < 5;
  }

  @override
  List<Object?> get props => [
        token,
        refreshToken,
        user,
        expiresAt,
        createdAt,
      ];
}

/// Credenciales de login - request value object
class LoginCredentials extends Equatable {
  final String username;
  final String password;

  const LoginCredentials({
    required this.username,
    required this.password,
  });

  @override
  List<Object?> get props => [username, password];
}
