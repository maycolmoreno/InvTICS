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
import 'features/gps/data/gps_repository.dart';
import 'features/gps/presentation/gps_provider.dart';
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
        ChangeNotifierProvider(
          create: (_) => GpsProvider(
            repository: GpsRepository(secureStorage: secureStorage),
          ),
        ),
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
    return Scaffold(
      body: Container(
        width: double.infinity,
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [Color(0xFF0D47A1), Color(0xFF185FA5), Color(0xFF1D9E75)],
          ),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 80,
              height: 80,
              decoration: BoxDecoration(
                color: Colors.white.withValues(alpha: 0.15),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(
                    color: Colors.white.withValues(alpha: 0.3), width: 2),
              ),
              child: const Icon(Icons.shield_outlined,
                  size: 40, color: Colors.white),
            ),
            const SizedBox(height: 20),
            const Text('CRESIO',
                style: TextStyle(
                    color: Colors.white,
                    fontSize: 32,
                    fontWeight: FontWeight.w800,
                    letterSpacing: 2)),
            const SizedBox(height: 6),
            Text('Gestion de Activos TI',
                style: TextStyle(
                    color: Colors.white.withValues(alpha: 0.8), fontSize: 14)),
            const SizedBox(height: 32),
            const SizedBox(
              width: 24,
              height: 24,
              child: CircularProgressIndicator(
                  strokeWidth: 2.5, color: Colors.white),
            ),
          ],
        ),
      ),
    );
  }
}
