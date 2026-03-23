class LoginRequest {
  final String username;
  final String password;

  const LoginRequest({
    required this.username,
    required this.password,
  });

  Map<String, dynamic> toJson() => {
        'username': username,
        'password': password,
      };
}

enum UserRole {
  admin,
  tecnico,
  consulta,
  unknown,
}

enum UserCapability {
  viewMantenimientos,
  createMantenimiento,
  closeMantenimiento,
  viewVisitas,
  viewEquipos,
  viewNotificaciones,
  manageUbicaciones,
  manageServerConfig,
}

class RoleCapabilities {
  const RoleCapabilities(this.values);

  final Set<UserCapability> values;

  bool has(UserCapability capability) => values.contains(capability);
}

class AuthSession {
  final String token;
  final String username;
  final String displayName;
  final String role;

  const AuthSession({
    required this.token,
    required this.username,
    required this.displayName,
    required this.role,
  });

  factory AuthSession.fromJson(Map<String, dynamic> json) {
    return AuthSession(
      token: json['token']?.toString() ?? json['jwt']?.toString() ?? '',
      username: json['username']?.toString() ?? json['usuario']?.toString() ?? '',
      displayName: json['nombre']?.toString() ??
          json['displayName']?.toString() ??
          json['username']?.toString() ??
          '',
      role: json['role']?.toString() ?? json['rol']?.toString() ?? '',
    );
  }

  String get normalizedRole => role.trim().toUpperCase();

  UserRole get userRole {
    final value = normalizedRole;
    if (value.contains('ADMIN')) {
      return UserRole.admin;
    }
    if (value.contains('TECNICO') || value.contains('SOPORTE')) {
      return UserRole.tecnico;
    }
    if (value.contains('CONSULTA') ||
        value.contains('AUDITOR') ||
        value.contains('INVITADO') ||
        value.contains('CUSTODIO')) {
      return UserRole.consulta;
    }
    return UserRole.unknown;
  }

  String get roleLabel {
    switch (userRole) {
      case UserRole.admin:
        return 'Administrador';
      case UserRole.tecnico:
        return 'Tecnico';
      case UserRole.consulta:
        return 'Consulta';
      case UserRole.unknown:
        return normalizedRole.isEmpty ? 'Sin rol' : normalizedRole;
    }
  }

  bool get isSupported => userRole != UserRole.unknown;

  RoleCapabilities get capabilities {
    switch (userRole) {
      case UserRole.admin:
        return const RoleCapabilities({
          UserCapability.viewMantenimientos,
          UserCapability.createMantenimiento,
          UserCapability.closeMantenimiento,
          UserCapability.viewVisitas,
          UserCapability.viewEquipos,
          UserCapability.viewNotificaciones,
          UserCapability.manageUbicaciones,
          UserCapability.manageServerConfig,
        });
      case UserRole.tecnico:
        return const RoleCapabilities({
          UserCapability.viewMantenimientos,
          UserCapability.createMantenimiento,
          UserCapability.closeMantenimiento,
          UserCapability.viewVisitas,
          UserCapability.viewEquipos,
          UserCapability.viewNotificaciones,
        });
      case UserRole.consulta:
        return const RoleCapabilities({
          UserCapability.viewMantenimientos,
          UserCapability.viewEquipos,
          UserCapability.viewNotificaciones,
        });
      case UserRole.unknown:
        return const RoleCapabilities({});
    }
  }
}
