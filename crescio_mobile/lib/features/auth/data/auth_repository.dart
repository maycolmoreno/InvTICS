import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/storage/secure_storage_service.dart';
import 'auth_models.dart';

class AuthRepository {
  AuthRepository({
    required ApiClient apiClient,
    required SecureStorageService secureStorage,
  })  : _apiClient = apiClient,
        _secureStorage = secureStorage;

  final ApiClient _apiClient;
  final SecureStorageService _secureStorage;

  Future<AuthSession> login(LoginRequest request) async {
    final data = await _apiClient.post('/auth/login', request.toJson());
    final session = AuthSession.fromJson(Map<String, dynamic>.from(data));
    if (session.token.isEmpty) {
      throw const AuthException('Credenciales incorrectas.');
    }
    await _secureStorage.saveSession(
      token: session.token,
      username: session.username,
      displayName: session.displayName,
      role: session.role,
    );
    return session;
  }

  Future<void> logout() => _secureStorage.clearSession();

  Future<AuthSession?> readStoredSession() async {
    final token = await _secureStorage.readToken();
    if (token == null || token.isEmpty) {
      return null;
    }
    final username = await _secureStorage.readUsername() ?? '';
    final displayName = await _secureStorage.readDisplayName() ?? username;
    final role = await _secureStorage.readRole() ?? '';
    return AuthSession(
      token: token,
      username: username,
      displayName: displayName,
      role: role,
    );
  }
}
