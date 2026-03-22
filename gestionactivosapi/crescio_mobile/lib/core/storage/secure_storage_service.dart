import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class SecureStorageService {
  static const _jwtKey = 'auth_jwt';
  static const _nameKey = 'auth_name';
  static const _roleKey = 'auth_role';
  static const _userKey = 'auth_user';

  final FlutterSecureStorage _storage;

  SecureStorageService({FlutterSecureStorage? storage})
      : _storage = storage ?? const FlutterSecureStorage();

  Future<void> saveSession({
    required String token,
    required String username,
    required String displayName,
    required String role,
  }) async {
    await _storage.write(key: _jwtKey, value: token);
    await _storage.write(key: _userKey, value: username);
    await _storage.write(key: _nameKey, value: displayName);
    await _storage.write(key: _roleKey, value: role);
  }

  Future<String?> readToken() => _storage.read(key: _jwtKey);
  Future<String?> readUsername() => _storage.read(key: _userKey);
  Future<String?> readDisplayName() => _storage.read(key: _nameKey);
  Future<String?> readRole() => _storage.read(key: _roleKey);

  Future<void> clearSession() async {
    await _storage.delete(key: _jwtKey);
    await _storage.delete(key: _userKey);
    await _storage.delete(key: _nameKey);
    await _storage.delete(key: _roleKey);
  }
}
