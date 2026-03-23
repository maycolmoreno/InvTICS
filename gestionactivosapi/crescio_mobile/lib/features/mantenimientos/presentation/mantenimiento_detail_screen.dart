import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:open_filex/open_filex.dart';
import 'package:path_provider/path_provider.dart';
import 'package:provider/provider.dart';

import '../../../core/network/api_client.dart';
import '../../auth/data/auth_models.dart';
import '../../auth/presentation/auth_provider.dart';
import '../data/mantenimientos_repository.dart';

class MantenimientoDetailScreen extends StatefulWidget {
  const MantenimientoDetailScreen({
    super.key,
    required this.mantenimientoId,
  });

  final int mantenimientoId;

  @override
  State<MantenimientoDetailScreen> createState() => _MantenimientoDetailScreenState();
}

class _MantenimientoDetailScreenState extends State<MantenimientoDetailScreen> {
  final _observacionesController = TextEditingController();
  late Future<Map<String, dynamic>> _future;
  Map<String, dynamic> _lastLoadedItem = const {};
  bool _closing = false;
  bool _sendingEmail = false;
  bool _downloadingPdf = false;

  @override
  void initState() {
    super.initState();
    _future = _load();
  }

  @override
  void dispose() {
    _observacionesController.dispose();
    super.dispose();
  }

  Future<Map<String, dynamic>> _load() {
    return MantenimientosRepository(context.read<ApiClient>())
        .obtenerDetalle(widget.mantenimientoId);
  }

  Future<void> _reload() async {
    final future = _load();
    setState(() => _future = future);
    await future;
  }

  Future<void> _cerrar() async {
    final observaciones = _observacionesController.text.trim();

    setState(() => _closing = true);
    try {
      final queuedOffline =
          await MantenimientosRepository(context.read<ApiClient>()).cerrarConFallback(
        mantenimientoId: widget.mantenimientoId,
        observaciones: observaciones,
      );
      _observacionesController.clear();
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            queuedOffline
                ? 'Sin conexion. El cierre quedo pendiente de sincronizacion.'
                : 'Mantenimiento cerrado correctamente.',
          ),
        ),
      );
      if (!queuedOffline) {
        try {
          await _reload();
        } catch (_) {
          if (!mounted) return;
          setState(() {
            _future = Future.value({
              ..._lastLoadedItem,
              'estadoInterno': 'CERRADO',
            });
          });
        }
      }
    } catch (_) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('No fue posible cerrar el mantenimiento.')),
      );
    } finally {
      if (mounted) {
        setState(() => _closing = false);
      }
    }
  }

  Future<void> _reenviarCorreo() async {
    setState(() => _sendingEmail = true);
    try {
      await MantenimientosRepository(context.read<ApiClient>()).reenviarCorreo(
        mantenimientoId: widget.mantenimientoId,
      );
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Correo reenviado correctamente.')),
      );
    } catch (_) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('No fue posible reenviar el correo.')),
      );
    } finally {
      if (mounted) {
        setState(() => _sendingEmail = false);
      }
    }
  }

  Future<void> _descargarPdf() async {
    setState(() => _downloadingPdf = true);
    try {
      final bytes = await MantenimientosRepository(context.read<ApiClient>()).descargarPdf(
        mantenimientoId: widget.mantenimientoId,
      );
      final directory = await getTemporaryDirectory();
      final file = File('${directory.path}/mantenimiento_${widget.mantenimientoId}.pdf');
      await file.writeAsBytes(bytes, flush: true);
      final result = await OpenFilex.open(file.path, type: 'application/pdf');
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            result.type == ResultType.done
                ? 'PDF descargado y abierto correctamente.'
                : 'PDF descargado en ${file.path}',
          ),
        ),
      );
    } catch (_) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('No fue posible descargar el PDF.')),
      );
    } finally {
      if (mounted) {
        setState(() => _downloadingPdf = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final canClose = context.watch<AuthProvider>().hasCapability(
      UserCapability.closeMantenimiento,
    );
    return Scaffold(
      appBar: AppBar(title: const Text('Detalle del mantenimiento')),
      body: FutureBuilder<Map<String, dynamic>>(
        future: _future,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }
          if (snapshot.hasError) {
            return _RetryState(
              message: 'No fue posible cargar el mantenimiento.',
              onRetry: _reload,
            );
          }

          final item = snapshot.data ?? const {};
          _lastLoadedItem = Map<String, dynamic>.from(item);
          final cerrado = _text(item['estadoInterno']).toUpperCase() == 'CERRADO';

          return RefreshIndicator(
            onRefresh: _reload,
            child: ListView(
              padding: const EdgeInsets.all(16),
              physics: const AlwaysScrollableScrollPhysics(),
              children: [
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          _text(item['equipoCodigoSap'], fallback: 'Sin codigo'),
                          style: Theme.of(context).textTheme.titleMedium,
                        ),
                        const SizedBox(height: 12),
                        _line('Equipo', item['equipoDescripcion']),
                        _line('Tecnico', item['tecnicoNombre']),
                        _line('Estado', item['estadoInterno']),
                        _line('Fecha', item['fechaMantenimiento']),
                        _line('SINE', item['sineSnapshoted']),
                        _line('Ticket', item['ticketId']),
                        _line('Descripcion', item['descripcion']),
                        _line('Trabajo realizado', item['descripcionTrabajoRealizado']),
                        const SizedBox(height: 8),
                        Wrap(
                          spacing: 12,
                          runSpacing: 12,
                          children: [
                            OutlinedButton.icon(
                              onPressed: _downloadingPdf ? null : _descargarPdf,
                              icon: _downloadingPdf
                                  ? const SizedBox(
                                      height: 16,
                                      width: 16,
                                      child: CircularProgressIndicator(strokeWidth: 2),
                                    )
                                  : const Icon(Icons.picture_as_pdf_outlined),
                              label: Text(_downloadingPdf ? 'Descargando...' : 'Descargar PDF'),
                            ),
                            OutlinedButton.icon(
                              onPressed: _sendingEmail ? null : _reenviarCorreo,
                              icon: _sendingEmail
                                  ? const SizedBox(
                                      height: 16,
                                      width: 16,
                                      child: CircularProgressIndicator(strokeWidth: 2),
                                    )
                                  : const Icon(Icons.mark_email_read_outlined),
                              label: Text(_sendingEmail ? 'Enviando...' : 'Reenviar correo'),
                            ),
                          ],
                        ),
                        const SizedBox(height: 12),
                        _SignaturePreview(
                          title: 'Firma del tecnico',
                          base64Value: item['firmaTecnico'],
                        ),
                        _SignaturePreview(
                          title: 'Firma del custodio',
                          base64Value: item['firmaCustodio'],
                        ),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 12),
                if (!cerrado && canClose) ...[
                  TextField(
                    controller: _observacionesController,
                    minLines: 3,
                    maxLines: 5,
                    decoration: const InputDecoration(
                      labelText: 'Observaciones de cierre',
                      hintText: 'Describe el trabajo realizado por el tecnico',
                    ),
                  ),
                  const SizedBox(height: 12),
                  FilledButton.icon(
                    onPressed: _closing ? null : _cerrar,
                    icon: _closing
                        ? const SizedBox(
                            height: 16,
                            width: 16,
                            child: CircularProgressIndicator(strokeWidth: 2),
                          )
                        : const Icon(Icons.check_circle_outline),
                    label: Text(_closing ? 'Cerrando...' : 'Cerrar mantenimiento'),
                  ),
                ] else if (cerrado)
                  const Card(
                    child: Padding(
                      padding: EdgeInsets.all(16),
                      child: Text('Este mantenimiento ya fue cerrado.'),
                    ),
                  )
                else
                  const Card(
                    child: Padding(
                      padding: EdgeInsets.all(16),
                      child: Text('Tu rol no puede cerrar mantenimientos desde la app.'),
                    ),
                  ),
              ],
            ),
          );
        },
      ),
    );
  }
}

class _RetryState extends StatelessWidget {
  const _RetryState({
    required this.message,
    required this.onRetry,
  });

  final String message;
  final Future<void> Function() onRetry;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(message),
          const SizedBox(height: 12),
          OutlinedButton(onPressed: onRetry, child: const Text('Reintentar')),
        ],
      ),
    );
  }
}

class _SignaturePreview extends StatelessWidget {
  const _SignaturePreview({
    required this.title,
    required this.base64Value,
  });

  final String title;
  final dynamic base64Value;

  @override
  Widget build(BuildContext context) {
    final cleanBase64 = _cleanBase64(base64Value);
    if (cleanBase64.isEmpty) {
      return const SizedBox.shrink();
    }

    Uint8List bytes;
    try {
      bytes = base64Decode(cleanBase64);
    } catch (_) {
      return Padding(
        padding: const EdgeInsets.only(bottom: 12),
        child: Text('$title: no disponible'),
      );
    }

    return Padding(
      padding: const EdgeInsets.only(top: 8, bottom: 12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: const TextStyle(fontWeight: FontWeight.w700),
          ),
          const SizedBox(height: 8),
          Container(
            width: double.infinity,
            height: 140,
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              border: Border.all(color: Colors.black12),
              borderRadius: BorderRadius.circular(12),
              color: Colors.white,
            ),
            child: Image.memory(
              bytes,
              fit: BoxFit.contain,
              errorBuilder: (context, error, stackTrace) => const Center(
                child: Text('No fue posible mostrar la firma.'),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

Widget _line(String label, dynamic value) {
  final text = _text(value, fallback: '');
  if (text.isEmpty) {
    return const SizedBox.shrink();
  }
  return Padding(
    padding: const EdgeInsets.only(bottom: 8),
    child: RichText(
      text: TextSpan(
        style: const TextStyle(color: Colors.black87),
        children: [
          TextSpan(
            text: '$label: ',
            style: const TextStyle(fontWeight: FontWeight.w700),
          ),
          TextSpan(text: text),
        ],
      ),
    ),
  );
}

String _text(dynamic value, {String fallback = '-'}) {
  final text = value?.toString().trim() ?? '';
  return text.isEmpty ? fallback : text;
}

String _cleanBase64(dynamic value) {
  final text = value?.toString().trim() ?? '';
  if (text.isEmpty) {
    return '';
  }
  final parts = text.split(',');
  return parts.isEmpty ? text : parts.last.trim();
}
