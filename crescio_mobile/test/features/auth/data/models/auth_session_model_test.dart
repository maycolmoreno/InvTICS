import 'package:flutter_test/flutter_test.dart';
import 'package:cresio_mobile/features/auth/data/models/auth_session_model.dart';
import 'package:cresio_mobile/core/domain/entities/auth_entity.dart';

void main() {
  group('AuthSessionModel', () {
    final testSessionJson = {
      'token': 'test-token',
      'refreshToken': 'test-refresh-token',
      'user': {
        'id': 'user-1',
        'username': 'testuser',
        'displayName': 'Test User',
        'email': 'test@example.com',
        'role': 'TECHNICIAN',
        'permissions': ['READ', 'WRITE'],
        'isActive': true,
      },
      'expiresAt': '2025-03-25T10:30:00.000Z',
      'createdAt': '2025-03-24T10:30:00.000Z',
    };

    test('fromJson creates model from JSON', () {
      final model = AuthSessionModel.fromJson(testSessionJson);

      expect(model.token, 'test-token');
      expect(model.refreshToken, 'test-refresh-token');
      expect(model.user.username, 'testuser');
      expect(model.user.role, 'TECHNICIAN');
    });

    test('fromJson with minimal data', () {
      final minimalJson = {
        'user': {'permissions': []},
      };

      final model = AuthSessionModel.fromJson(minimalJson);

      expect(model.token, '');
      expect(model.refreshToken, '');
      expect(model.user.username, '');
    });

    test('toJson serializes model to JSON', () {
      final model = AuthSessionModel.fromJson(testSessionJson);
      final json = model.toJson();

      expect(json['token'], 'test-token');
      expect(json['refreshToken'], 'test-refresh-token');
      expect(json['user']['username'], 'testuser');
      expect(json['user']['role'], 'TECHNICIAN');
    });

    test('JSON round-trip conversion maintains data', () {
      final model = AuthSessionModel.fromJson(testSessionJson);
      final json = model.toJson();
      final reconstructed = AuthSessionModel.fromJson(json);

      expect(reconstructed.token, model.token);
      expect(reconstructed.refreshToken, model.refreshToken);
      expect(reconstructed.user.username, model.user.username);
      expect(reconstructed.user.email, model.user.email);
    });

    test('Inherits from AuthSession entity', () {
      final model = AuthSessionModel.fromJson(testSessionJson);

      expect(model, isA<AuthSession>());
    });

    test('AuthSessionModel with null expiresAt gets default date', () {
      final json = {
        'token': 'test-token',
        'refreshToken': 'test-refresh-token',
        'user': {'permissions': []},
        'createdAt': DateTime.now().toIso8601String(),
      };

      final model = AuthSessionModel.fromJson(json);

      expect(model.expiresAt, isNotNull);
      expect(model.expiresAt.isAfter(DateTime.now()), true);
    });

    test('AuthSessionModel isExpired property works correctly', () {
      final expiredJson = {
        'token': 'expired',
        'refreshToken': 'refresh',
        'user': {'permissions': []},
        'expiresAt': DateTime.now().subtract(Duration(hours: 1)).toIso8601String(),
        'createdAt': DateTime.now().toIso8601String(),
      };

      final model = AuthSessionModel.fromJson(expiredJson);
      expect(model.isExpired, true);
    });

    test('AuthSessionModel isExpiringSoon property works correctly', () {
      final expiringJson = {
        'token': 'expiring',
        'refreshToken': 'refresh',
        'user': {'permissions': []},
        'expiresAt': DateTime.now().add(Duration(minutes: 3)).toIso8601String(),
        'createdAt': DateTime.now().toIso8601String(),
      };

      final model = AuthSessionModel.fromJson(expiringJson);
      expect(model.isExpiringSoon, true);
    });
  });

  group('AuthUserModel', () {
    final testUserJson = {
      'id': 'user-1',
      'username': 'testuser',
      'displayName': 'Test User',
      'email': 'test@example.com',
      'role': 'TECHNICIAN',
      'permissions': ['READ', 'WRITE', 'DELETE'],
      'createdAt': '2025-01-24T10:30:00.000Z',
      'isActive': true,
    };

    test('fromJson creates model from JSON', () {
      final model = AuthUserModel.fromJson(testUserJson);

      expect(model.id, 'user-1');
      expect(model.username, 'testuser');
      expect(model.displayName, 'Test User');
      expect(model.email, 'test@example.com');
      expect(model.role, 'TECHNICIAN');
      expect(model.permissions.length, 3);
      expect(model.isActive, true);
    });

    test('fromJson with empty permissions list', () {
      final json = {
        'id': 'user-1',
        'username': 'newuser',
        'displayName': 'New User',
        'email': 'new@example.com',
        'role': 'VIEWER',
        'permissions': [],
      };

      final model = AuthUserModel.fromJson(json);
      expect(model.permissions.isEmpty, true);
    });

    test('toJson serializes model to JSON', () {
      final model = AuthUserModel.fromJson(testUserJson);
      final json = model.toJson();

      expect(json['id'], 'user-1');
      expect(json['username'], 'testuser');
      expect(json['email'], 'test@example.com');
      expect(json['permissions'].length, 3);
    });

    test('JSON round-trip conversion maintains data', () {
      final model = AuthUserModel.fromJson(testUserJson);
      final json = model.toJson();
      final reconstructed = AuthUserModel.fromJson(json);

      expect(reconstructed.id, model.id);
      expect(reconstructed.username, model.username);
      expect(reconstructed.email, model.email);
      expect(reconstructed.permissions, model.permissions);
    });

    test('Inherits from AuthUser entity', () {
      final model = AuthUserModel.fromJson(testUserJson);
      expect(model, isA<AuthUser>());
    });

    test('AuthUserModel with null createdAt', () {
      final json = {
        'id': 'user-1',
        'username': 'testuser',
        'displayName': 'Test User',
        'email': 'test@example.com',
        'role': 'TECHNICIAN',
        'permissions': [],
      };

      final model = AuthUserModel.fromJson(json);
      expect(model.createdAt, null);
    });

    test('AuthUserModel defaults to isActive=true', () {
      final json = {
        'id': 'user-1',
        'username': 'testuser',
        'displayName': 'Test User',
        'email': 'test@example.com',
        'role': 'TECHNICIAN',
        'permissions': [],
      };

      final model = AuthUserModel.fromJson(json);
      expect(model.isActive, true);
    });

    test('AuthUserModel with isActive=false', () {
      final json = {
        'id': 'user-1',
        'username': 'testuser',
        'displayName': 'Test User',
        'email': 'test@example.com',
        'role': 'TECHNICIAN',
        'permissions': [],
        'isActive': false,
      };

      final model = AuthUserModel.fromJson(json);
      expect(model.isActive, false);
    });
  });
}
