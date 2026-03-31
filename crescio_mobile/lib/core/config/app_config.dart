import 'package:shared_preferences/shared_preferences.dart';

class AppConfig {
  static const _defaultServerIp = '192.168.2.242';
  static const _defaultServerPort = 8083;
  static String _serverIp = _defaultServerIp;
  static int _serverPort = _defaultServerPort;
  static bool _configured = false;

  static String get serverIp => _serverIp;
  static int get serverPort => _serverPort;
  static bool get isConfigured => _configured;
  static String get baseUrl => 'http://$_serverIp:$_serverPort/api';

  static Future<void> load() async {
    final prefs = await SharedPreferences.getInstance();
    _serverIp = prefs.getString('server_ip') ?? _defaultServerIp;
    _serverPort = prefs.getInt('server_port') ?? _defaultServerPort;
    _configured = prefs.getBool('server_configured') ?? false;
  }

  static Future<void> save(String ip, int port) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('server_ip', ip);
    await prefs.setInt('server_port', port);
    await prefs.setBool('server_configured', true);
    _serverIp = ip;
    _serverPort = port;
    _configured = true;
  }
}
