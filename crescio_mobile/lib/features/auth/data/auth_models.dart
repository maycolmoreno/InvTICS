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
}
