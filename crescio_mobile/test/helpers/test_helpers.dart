import 'package:mockito/annotations.dart';
import 'package:http/http.dart' as http;
import 'package:cresio_mobile/core/network/api_client.dart';
import 'package:cresio_mobile/core/storage/secure_storage_service.dart';
import 'package:cresio_mobile/features/auth/data/datasources/auth_remote_datasource.dart';
import 'package:cresio_mobile/features/auth/data/datasources/auth_local_datasource.dart';
import 'package:cresio_mobile/core/domain/repositories/auth_repository.dart';

@GenerateMocks([
  http.Client,
  SecureStorageService,
  ApiClient,
  AuthRemoteDatasource,
  AuthLocalDatasource,
  AuthRepository,
])
void main() {}
