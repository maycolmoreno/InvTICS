import '../../../core/domain/entities/auth_entity.dart';

/// Modelo de datos para Sesión de Autenticación
/// Extiende el entity del dominio para agregar capacidades de serialización
class AuthSessionModel extends AuthSession {
  const AuthSessionModel({
    required String token,
    required String refreshToken,
    required AuthUser user,
    required DateTime expiresAt,
    required DateTime createdAt,
  }) : super(
          token: token,
          refreshToken: refreshToken,
          user: user,
          expiresAt: expiresAt,
          createdAt: createdAt,
        );

  /// Crea una instancia desde JSON del servidor
  factory AuthSessionModel.fromJson(Map<String, dynamic> json) {
    return AuthSessionModel(
      token: json['token']?.toString() ?? '',
      refreshToken: json['refreshToken']?.toString() ?? '',
      user: AuthUserModel.fromJson(json['user'] ?? {}),
      expiresAt: json['expiresAt'] != null
          ? DateTime.parse(json['expiresAt'].toString())
          : DateTime.now().add(const Duration(hours: 24)),
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'].toString())
          : DateTime.now(),
    );
  }

  /// Convierte a JSON para almacenamiento
  Map<String, dynamic> toJson() => {
        'token': token,
        'refreshToken': refreshToken,
        'user': _userToJson(user),
        'expiresAt': expiresAt.toIso8601String(),
        'createdAt': createdAt.toIso8601String(),
      };

  Map<String, dynamic> _userToJson(AuthUser user) => {
        'id': user.id,
        'username': user.username,
        'displayName': user.displayName,
        'email': user.email,
        'role': user.role,
        'permissions': user.permissions,
        'createdAt': user.createdAt?.toIso8601String(),
        'isActive': user.isActive,
      };
}

/// Modelo de datos para Usuario
class AuthUserModel extends AuthUser {
  const AuthUserModel({
    required String id,
    required String username,
    required String displayName,
    required String email,
    required String role,
    required List<String> permissions,
    DateTime? createdAt,
    bool isActive = true,
  }) : super(
          id: id,
          username: username,
          displayName: displayName,
          email: email,
          role: role,
          permissions: permissions,
          createdAt: createdAt,
          isActive: isActive,
        );

  /// Crea una instancia desde JSON
  factory AuthUserModel.fromJson(Map<String, dynamic> json) {
    return AuthUserModel(
      id: json['id']?.toString() ?? '',
      username: json['username']?.toString() ?? '',
      displayName: json['displayName']?.toString() ?? '',
      email: json['email']?.toString() ?? '',
      role: json['role']?.toString() ?? '',
      permissions: List<String>.from(json['permissions'] ?? []),
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'].toString())
          : null,
      isActive: json['isActive'] as bool? ?? true,
    );
  }

  /// Convierte a JSON
  Map<String, dynamic> toJson() => {
        'id': id,
        'username': username,
        'displayName': displayName,
        'email': email,
        'role': role,
        'permissions': permissions,
        'createdAt': createdAt?.toIso8601String(),
        'isActive': isActive,
      };
}
