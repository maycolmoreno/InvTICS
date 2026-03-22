import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'auth_provider.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key, this.message});

  final String? message;

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _userController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _obscureText = true;

  @override
  void dispose() {
    _userController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthProvider>();
    final isLoading = auth.status == AuthStatus.loading;
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 420),
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(24),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      const Icon(Icons.shield_outlined, size: 72, color: Color(0xFF185FA5)),
                      const SizedBox(height: 12),
                      const Text(
                        'CRESIO',
                        textAlign: TextAlign.center,
                        style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 4),
                      const Text(
                        'Acceso para tecnicos de soporte',
                        textAlign: TextAlign.center,
                        style: TextStyle(fontSize: 16),
                      ),
                      const SizedBox(height: 24),
                      TextField(
                        controller: _userController,
                        decoration: const InputDecoration(
                          labelText: 'Correo',
                          hintText: 'tecnico@empresa.com',
                          prefixIcon: Icon(Icons.person_outline),
                        ),
                      ),
                      const SizedBox(height: 16),
                      TextField(
                        controller: _passwordController,
                        obscureText: _obscureText,
                        decoration: InputDecoration(
                          labelText: 'Contrasena',
                          prefixIcon: const Icon(Icons.lock_outline),
                          suffixIcon: IconButton(
                            onPressed: () => setState(() => _obscureText = !_obscureText),
                            icon: Icon(
                              _obscureText ? Icons.visibility_outlined : Icons.visibility_off_outlined,
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(height: 16),
                      if (widget.message != null || auth.errorMessage != null)
                        Text(
                          auth.errorMessage ?? widget.message!,
                          style: TextStyle(color: Colors.red.shade700, fontSize: 16),
                        ),
                      const SizedBox(height: 20),
                      ElevatedButton(
                        onPressed: isLoading ? null : _submit,
                        child: Text(isLoading ? 'Ingresando...' : 'Iniciar sesion'),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  Future<void> _submit() async {
    final auth = context.read<AuthProvider>();
    await auth.login(
      _userController.text.trim(),
      _passwordController.text,
    );
  }
}
