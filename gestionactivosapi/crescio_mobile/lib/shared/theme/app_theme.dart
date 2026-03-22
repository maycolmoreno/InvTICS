import 'package:flutter/material.dart';

class AppTheme {
  static const _primary = Color(0xFF185FA5);
  static const _secondary = Color(0xFF1D9E75);
  static const _error = Color(0xFFE24B4A);
  static const _background = Color(0xFFF5F5F5);
  static const _surface = Colors.white;

  static ThemeData get light {
    final colorScheme = const ColorScheme.light(
      primary: _primary,
      onPrimary: Colors.white,
      secondary: _secondary,
      error: _error,
      onError: Colors.white,
      surface: _surface,
      onSurface: Color(0xFF1E1E1E),
    ).copyWith(surface: _surface);

    return ThemeData(
      useMaterial3: true,
      colorScheme: colorScheme,
      scaffoldBackgroundColor: _background,
      appBarTheme: const AppBarTheme(
        backgroundColor: _primary,
        foregroundColor: Colors.white,
        centerTitle: false,
      ),
      textTheme: Typography.blackMountainView.apply(fontSizeFactor: 1.0).copyWith(
            bodyMedium: const TextStyle(fontSize: 16),
            bodyLarge: const TextStyle(fontSize: 16),
            titleMedium: const TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
          ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          minimumSize: const Size.fromHeight(48),
          backgroundColor: _primary,
          foregroundColor: Colors.white,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
          textStyle: const TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
        ),
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: Colors.white,
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: Color(0xFFD8DDE3)),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: Color(0xFFD8DDE3)),
        ),
      ),
      cardTheme: CardTheme(
        elevation: 0,
        color: Colors.white,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
          side: const BorderSide(color: Color(0xFFE1E5EA), width: 0.5),
        ),
        margin: const EdgeInsets.symmetric(vertical: 6),
      ),
      listTileTheme: const ListTileThemeData(
        contentPadding: EdgeInsets.symmetric(horizontal: 16),
        minVerticalPadding: 12,
      ),
      snackBarTheme: const SnackBarThemeData(
        behavior: SnackBarBehavior.floating,
      ),
    );
  }
}
