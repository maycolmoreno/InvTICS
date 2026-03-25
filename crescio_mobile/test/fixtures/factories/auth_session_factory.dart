import 'package:cresio_mobile/core/domain/entities/auth_entity.dart';

/// Factory para crear instancias de prueba de AuthSession
class AuthSessionFactory {
  static AuthSession createSession({
    String token = 'test-jwt-token',
    String refreshToken = 'test-refresh-token',
    String userId = 'user-123',
    String username = 'testuser',
    String displayName = 'Test User',
    String email = 'test@example.com',
    String role = 'TECHNICIAN',
    List<String>? permissions,
    DateTime? expiresAt,
    DateTime? createdAt,
    bool isActive = true,
  }) {
    final now = DateTime.now();
    return AuthSession(
      token: token,
      refreshToken: refreshToken,
      user: AuthUser(
        id: userId,
        username: username,
        displayName: displayName,
        email: email,
        role: role,
        permissions: permissions ?? ['READ', 'CREATE', 'UPDATE'],
        createdAt: createdAt ?? now,
        isActive: isActive,
      ),
      expiresAt: expiresAt ?? now.add(const Duration(hours: 24)),
      createdAt: createdAt ?? now,
    );
  }

  static AuthSession createExpiredSession() {
    final now = DateTime.now();
    return AuthSession(
      token: 'expired-token',
      refreshToken: 'expired-refresh-token',
      user: createAuthUser(),
      expiresAt: now.subtract(const Duration(hours: 1)),
      createdAt: now.subtract(const Duration(days: 2)),
    );
  }

  static AuthSession createExpiringSession() {
    final now = DateTime.now();
    return AuthSession(
      token: 'expiring-token',
      refreshToken: 'expiring-refresh-token',
      user: createAuthUser(),
      expiresAt: now.add(const Duration(minutes: 3)),
      createdAt: now.subtract(const Duration(hours: 21)),
    );
  }

  static AuthUser createAuthUser({
    String id = 'user-123',
    String username = 'testuser',
    String displayName = 'Test User',
    String email = 'test@example.com',
    String role = 'TECHNICIAN',
    List<String>? permissions,
    DateTime? createdAt,
    bool isActive = true,
  }) {
    return AuthUser(
      id: id,
      username: username,
      displayName: displayName,
      email: email,
      role: role,
      permissions: permissions ?? ['READ', 'CREATE', 'UPDATE'],
      createdAt: createdAt ?? DateTime.now(),
      isActive: isActive,
    );
  }

  static LoginCredentials createCredentials({
    String username = 'testuser',
    String password = 'password123',
  }) {
    return LoginCredentials(
      username: username,
      password: password,
    );
  }
}
