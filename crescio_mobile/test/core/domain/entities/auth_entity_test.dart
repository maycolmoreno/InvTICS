import 'package:flutter_test/flutter_test.dart';
import 'package:cresio_mobile/core/domain/entities/auth_entity.dart';

void main() {
  group('AuthUser Entity', () {
    test('Creates AuthUser with correct properties', () {
      const user = AuthUser(
        id: 'user-1',
        username: 'testuser',
        displayName: 'Test User',
        email: 'test@example.com',
        role: 'TECHNICIAN',
        permissions: ['READ', 'WRITE'],
        isActive: true,
      );

      expect(user.id, 'user-1');
      expect(user.username, 'testuser');
      expect(user.displayName, 'Test User');
      expect(user.email, 'test@example.com');
      expect(user.role, 'TECHNICIAN');
      expect(user.permissions.length, 2);
      expect(user.isActive, true);
    });

    test('Compares two identical AuthUser instances correctly', () {
      const user1 = AuthUser(
        id: 'user-1',
        username: 'testuser',
        displayName: 'Test User',
        email: 'test@example.com',
        role: 'TECHNICIAN',
        permissions: ['READ', 'WRITE'],
        isActive: true,
      );

      const user2 = AuthUser(
        id: 'user-1',
        username: 'testuser',
        displayName: 'Test User',
        email: 'test@example.com',
        role: 'TECHNICIAN',
        permissions: ['READ', 'WRITE'],
        isActive: true,
      );

      expect(user1, equals(user2));
    });

    test('AuthUser with different properties are not equal', () {
      const user1 = AuthUser(
        id: 'user-1',
        username: 'testuser',
        displayName: 'Test User',
        email: 'test@example.com',
        role: 'TECHNICIAN',
        permissions: ['READ', 'WRITE'],
      );

      const user2 = AuthUser(
        id: 'user-2',
        username: 'otheruser',
        displayName: 'Other User',
        email: 'other@example.com',
        role: 'ADMIN',
        permissions: ['READ', 'WRITE', 'DELETE'],
      );

      expect(user1, isNot(equals(user2)));
    });

    test('AuthUser defaults to isActive=true', () {
      const user = AuthUser(
        id: 'user-1',
        username: 'testuser',
        displayName: 'Test User',
        email: 'test@example.com',
        role: 'TECHNICIAN',
        permissions: ['READ'],
      );

      expect(user.isActive, true);
    });
  });

  group('AuthSession Entity', () {
    final now = DateTime.now();

    test('Creates AuthSession with correct properties', () {
      final user = AuthUser(
        id: 'user-1',
        username: 'testuser',
        displayName: 'Test User',
        email: 'test@example.com',
        role: 'TECHNICIAN',
        permissions: ['READ'],
      );

      final session = AuthSession(
        token: 'token-123',
        refreshToken: 'refresh-123',
        user: user,
        expiresAt: now.add(const Duration(hours: 24)),
        createdAt: now,
      );

      expect(session.token, 'token-123');
      expect(session.refreshToken, 'refresh-123');
      expect(session.user, user);
      expect(session.createdAt, now);
    });

    test('AuthSession.isExpired returns true for expired token', () {
      final user = AuthUser(
        id: 'user-1',
        username: 'testuser',
        displayName: 'Test User',
        email: 'test@example.com',
        role: 'TECHNICIAN',
        permissions: ['READ'],
      );

      final session = AuthSession(
        token: 'expired-token',
        refreshToken: 'refresh-123',
        user: user,
        expiresAt: now.subtract(const Duration(hours: 1)),
        createdAt: now.subtract(const Duration(days: 2)),
      );

      expect(session.isExpired, true);
    });

    test('AuthSession.isExpired returns false for valid token', () {
      final user = AuthUser(
        id: 'user-1',
        username: 'testuser',
        displayName: 'Test User',
        email: 'test@example.com',
        role: 'TECHNICIAN',
        permissions: ['READ'],
      );

      final session = AuthSession(
        token: 'valid-token',
        refreshToken: 'refresh-123',
        user: user,
        expiresAt: now.add(const Duration(hours: 24)),
        createdAt: now,
      );

      expect(session.isExpired, false);
    });

    test('AuthSession.isExpiringSoon returns true for token expiring in 3 mins',
        () {
      final user = AuthUser(
        id: 'user-1',
        username: 'testuser',
        displayName: 'Test User',
        email: 'test@example.com',
        role: 'TECHNICIAN',
        permissions: ['READ'],
      );

      final session = AuthSession(
        token: 'expiring-token',
        refreshToken: 'refresh-123',
        user: user,
        expiresAt: now.add(const Duration(minutes: 3)),
        createdAt: now,
      );

      expect(session.isExpiringSoon, true);
    });

    test(
        'AuthSession.isExpiringSoon returns false for token expiring in 10 mins',
        () {
      final user = AuthUser(
        id: 'user-1',
        username: 'testuser',
        displayName: 'Test User',
        email: 'test@example.com',
        role: 'TECHNICIAN',
        permissions: ['READ'],
      );

      final session = AuthSession(
        token: 'valid-token',
        refreshToken: 'refresh-123',
        user: user,
        expiresAt: now.add(const Duration(minutes: 10)),
        createdAt: now,
      );

      expect(session.isExpiringSoon, false);
    });

    test('Compares two identical AuthSession instances correctly', () {
      final user = AuthUser(
        id: 'user-1',
        username: 'testuser',
        displayName: 'Test User',
        email: 'test@example.com',
        role: 'TECHNICIAN',
        permissions: ['READ'],
      );

      final session1 = AuthSession(
        token: 'token-123',
        refreshToken: 'refresh-123',
        user: user,
        expiresAt: now.add(const Duration(hours: 24)),
        createdAt: now,
      );

      final session2 = AuthSession(
        token: 'token-123',
        refreshToken: 'refresh-123',
        user: user,
        expiresAt: now.add(const Duration(hours: 24)),
        createdAt: now,
      );

      expect(session1, equals(session2));
    });
  });

  group('LoginCredentials Entity', () {
    test('Creates LoginCredentials with username and password', () {
      const credentials = LoginCredentials(
        username: 'testuser',
        password: 'password123',
      );

      expect(credentials.username, 'testuser');
      expect(credentials.password, 'password123');
    });

    test('Compares two identical LoginCredentials instances correctly', () {
      const cred1 = LoginCredentials(
        username: 'testuser',
        password: 'password123',
      );

      const cred2 = LoginCredentials(
        username: 'testuser',
        password: 'password123',
      );

      expect(cred1, equals(cred2));
    });

    test('LoginCredentials with different properties are not equal', () {
      const cred1 = LoginCredentials(
        username: 'testuser',
        password: 'password123',
      );

      const cred2 = LoginCredentials(
        username: 'testuser',
        password: 'password456',
      );

      expect(cred1, isNot(equals(cred2)));
    });
  });
}
