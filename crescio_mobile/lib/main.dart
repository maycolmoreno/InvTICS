import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'core/config/app_config.dart';
import 'core/network/api_client.dart';
import 'core/storage/local_database.dart';
import 'core/storage/secure_storage_service.dart';
import 'features/auth/data/auth_repository.dart';
import 'features/auth/presentation/auth_provider.dart';
import 'features/auth/presentation/login_screen.dart';
import 'features/configuracion/presentation/server_config_provider.dart';
import 'features/configuracion/presentation/server_config_screen.dart';
import 'features/dashboard/presentation/dashboard_shell.dart';
import 'shared/theme/app_theme.dart';

final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await AppConfig.load();
  await LocalDatabase.instance.database;

  final secureStorage = SecureStorageService();
  late final AuthProvider authProvider;
  final apiClient = ApiClient(
    secureStorage: secureStorage,
    onUnauthorized: () async {
      await authProvider.logout();
    },
  );
  authProvider = AuthProvider(
    repository: AuthRepository(
      secureStorage: secureStorage,
    ),
  );
  await authProvider.bootstrap(hasServerConfig: AppConfig.isConfigured);

  runApp(
    MultiProvider(
      providers: [
        Provider.value(value: secureStorage),
        Provider.value(value: apiClient),
        ChangeNotifierProvider.value(value: authProvider),
        ChangeNotifierProvider(create: (_) => ServerConfigProvider()),
      ],
      child: const CresioApp(),
    ),
  );
}

class CresioApp extends StatelessWidget {
  const CresioApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'CRESIO Mobile',
      debugShowCheckedModeBanner: false,
      navigatorKey: navigatorKey,
      theme: AppTheme.light,
      home: Consumer<AuthProvider>(
        builder: (context, auth, _) {
          switch (auth.status) {
            case AuthStatus.loading:
              return const _SplashScreen();
            case AuthStatus.requiresServerConfig:
              return const ServerConfigScreen(showContinue: true);
            case AuthStatus.authenticated:
              return const DashboardShell();
            case AuthStatus.unauthenticated:
              return LoginScreen(message: auth.errorMessage);
          }
        },
      ),
    );
  }
}

class _SplashScreen extends StatelessWidget {
  const _SplashScreen();

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(
        child: CircularProgressIndicator(),
      ),
    );
  }
}
