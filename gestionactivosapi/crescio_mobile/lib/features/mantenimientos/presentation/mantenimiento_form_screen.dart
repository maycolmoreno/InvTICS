import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';
import 'package:signature/signature.dart';

import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../auth/data/auth_models.dart';
import '../../auth/presentation/auth_provider.dart';
import '../../equipos/data/equipo_models.dart';
import '../../equipos/data/equipos_repository.dart';
import '../data/mantenimientos_repository.dart';

class MantenimientoFormScreen extends StatefulWidget {
  const MantenimientoFormScreen({
    super.key,
    this.initialEquipoIds = const <int>[],
  });

  final List<int> initialEquipoIds;

  @override
  State<MantenimientoFormScreen> createState() => _MantenimientoFormScreenState();
}

class _MantenimientoFormScreenState extends State<MantenimientoFormScreen> {
  final _formKey = GlobalKey<FormState>();
  final _detalleController = TextEditingController();
  final _custodioController = TextEditingController();
  final _picker = ImagePicker();
  final _firmaTecnicoController = SignatureController(
    penStrokeWidth: 2,
    penColor: Colors.black,
    exportBackgroundColor: Colors.white,
  );
  final _firmaCustodioController = SignatureController(
    penStrokeWidth: 2,
    penColor: Colors.black,
    exportBackgroundColor: Colors.white,
  );
  DateTime _fecha = DateTime.now();
  String _tipo = 'PREVENTIVO';
  String _estadoGeneral = 'OPERATIVO';
  final Set<int> _equipoIds = <int>{};
  int? _custodioId;
  bool _loading = true;
  bool _saving = false;
  List<EquipoListItem> _equipos = const [];
  List<Map<String, dynamic>> _custodios = const [];
  List<Map<String, dynamic>> _custodias = const [];
  List<Map<String, dynamic>> _actividades = const [];
  List<XFile> _imagenes = const [];
  final Set<int> _actividadesSeleccionadas = <int>{};
  final Map<String, TextEditingController> _observacionControllers = {};

  @override
  void initState() {
    super.initState();
    _equipoIds.addAll(widget.initialEquipoIds);
    _loadCatalogs();
  }

  @override
  void dispose() {
    _detalleController.dispose();
    _custodioController.dispose();
    _firmaTecnicoController.dispose();
    _firmaCustodioController.dispose();
    for (final controller in _observacionControllers.values) {
      controller.dispose();
    }
    super.dispose();
  }

  Future<void> _loadCatalogs() async {
    setState(() => _loading = true);
    try {
      final apiClient = context.read<ApiClient>();
      final repository = MantenimientosRepository(apiClient);
      final equipos = await EquiposRepository(apiClient).listar();
      final custodios = await repository.listarCustodios();
      final custodias = await repository.listarCustodias();
      final actividades = await repository.listarActividadesChecklist();
      if (!mounted) return;
      final categories = actividades
          .map((item) => _text(item['categoria'], fallback: 'General'))
          .toSet();
      for (final category in categories) {
        _observacionControllers.putIfAbsent(category, TextEditingController.new);
      }
      setState(() {
        _equipos = equipos;
        _custodios = custodios;
        _custodias = custodias;
        _actividades = actividades;
        _ensureValidCustodioSelection();
      });
    } on OfflineException catch (error) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(error.message)),
      );
      setState(() {
        _equipos = const [];
        _custodios = const [];
        _custodias = const [];
        _actividades = const [];
        _custodioId = null;
        _equipoIds.clear();
      });
    } finally {
      if (mounted) {
        setState(() => _loading = false);
      }
    }
  }

  Future<void> _pickImages() async {
    final images = await _picker.pickMultiImage(imageQuality: 80);
    if (images.isEmpty) {
      return;
    }
    setState(() => _imagenes = [..._imagenes, ...images]);
  }

  Future<void> _selectDate() async {
    final picked = await showDatePicker(
      context: context,
      initialDate: _fecha,
      firstDate: DateTime(2020),
      lastDate: DateTime(2100),
    );
    if (picked != null) {
      setState(() => _fecha = picked);
    }
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }
    if (_equipoIds.isEmpty || _custodioId == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Selecciona al menos un equipo y un custodio.')),
      );
      return;
    }
    final equiposInvalidos = _equipoIds.where((equipoId) => !_equipoPerteneceACustodio(equipoId, _custodioId!));
    if (equiposInvalidos.isNotEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Uno o mas equipos no pertenecen al custodio indicado.'),
        ),
      );
      return;
    }
    if (_actividades.isEmpty || _actividadesSeleccionadas.length != _actividades.length) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Debes completar todo el checklist.')),
      );
      return;
    }
    if (_firmaTecnicoController.isEmpty || _firmaCustodioController.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Debes registrar la firma del tecnico y del custodio.')),
      );
      return;
    }

    setState(() => _saving = true);
    try {
      final repository = MantenimientosRepository(context.read<ApiClient>());
      final firmaTecnico = await _signatureBase64(_firmaTecnicoController);
      final firmaCustodio = await _signatureBase64(_firmaCustodioController);
      final payloadImagenes = <Map<String, dynamic>>[];
      for (final image in _imagenes) {
        final file = File(image.path);
        payloadImagenes.add({
          'nombreArchivo': image.name,
          'rutaArchivo': image.path,
          'tamanioBytes': await file.length(),
        });
      }
      final queuedOffline = await repository.crearVariosConFallback(
        equipoIds: _equipoIds.toList(),
        custodioId: _custodioId!,
        tipoMantenimiento: _tipo,
        fechaMantenimiento: _formatDate(_fecha),
        detalle: _buildDetalleCompleto(),
        estadoGeneral: _estadoGeneral,
        actividades: _buildActividadesPayload(),
        imagenes: payloadImagenes,
        firmaTecnico: firmaTecnico,
        firmaCustodio: firmaCustodio,
      );

      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            queuedOffline
                ? 'Sin conexion. El mantenimiento quedo pendiente de sincronizacion.'
                : 'Mantenimiento creado correctamente.',
          ),
        ),
      );
      Navigator.of(context).pop(true);
    } catch (_) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('No fue posible crear el mantenimiento.')),
      );
    } finally {
      if (mounted) {
        setState(() => _saving = false);
      }
    }
  }

  List<Widget> _buildChecklistWidgets() {
    final grouped = <String, List<Map<String, dynamic>>>{};
    for (final item in _actividades) {
      final category = _text(item['categoria'], fallback: 'General');
      grouped.putIfAbsent(category, () => <Map<String, dynamic>>[]).add(item);
    }

    return grouped.entries
        .map(
          (entry) => Card(
            child: Padding(
              padding: const EdgeInsets.all(12),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    entry.key,
                    style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w700),
                  ),
                  const SizedBox(height: 8),
                  ...entry.value.map((item) {
                    final actividadId = _asInt(item['idActividad']) ?? 0;
                    final selected = _actividadesSeleccionadas.contains(actividadId);
                    return CheckboxListTile(
                      contentPadding: EdgeInsets.zero,
                      value: selected,
                      title: Text(_text(item['nombre'], fallback: 'Actividad')),
                      subtitle: Text('Orden ${_text(item['orden'], fallback: '-')}'),
                      onChanged: (value) {
                        setState(() {
                          if (value == true) {
                            _actividadesSeleccionadas.add(actividadId);
                          } else {
                            _actividadesSeleccionadas.remove(actividadId);
                          }
                        });
                      },
                    );
                  }),
                ],
              ),
            ),
          ),
        )
        .toList();
  }

  List<Map<String, dynamic>> _buildActividadesPayload() {
    return _actividades.map((item) {
      final actividadId = _asInt(item['idActividad']) ?? 0;
      return {
        'idActividad': actividadId,
        'nombreActividad': _text(item['nombre'], fallback: 'Actividad'),
        'categoriaActividad': _text(item['categoria'], fallback: 'General'),
        'realizada': _actividadesSeleccionadas.contains(actividadId),
      };
    }).toList();
  }

  bool _equipoPerteneceACustodio(int equipoId, int custodioId) {
    return _custodias.any((custodia) {
      final estado = custodia['estado'] == true;
      final fkEquipo = Map<String, dynamic>.from(custodia['fkEquipo'] as Map? ?? const {});
      final fkCustodio =
          Map<String, dynamic>.from(custodia['fkCustodio'] as Map? ?? const {});
      final custodioMatch = _asInt(fkCustodio['idCustodio']) == custodioId ||
          _asInt(custodia['idCustodio']) == custodioId;
      final equipoMatch = _asInt(fkEquipo['idEquipo']) == equipoId;
      return estado && custodioMatch && equipoMatch;
    });
  }

  Set<int> _custodiosValidos() {
    if (_equipoIds.isEmpty) {
      return _custodios
          .map((item) => _asInt(item['idCustodio']) ?? _asInt(item['id']))
          .whereType<int>()
          .toSet();
    }

    final validosPorEquipo = _equipoIds.map((equipoId) {
      return _custodias
          .where((custodia) {
            final estado = custodia['estado'] == true;
            final fkEquipo = Map<String, dynamic>.from(custodia['fkEquipo'] as Map? ?? const {});
            return estado && _asInt(fkEquipo['idEquipo']) == equipoId;
          })
          .map((custodia) {
            final fkCustodio =
                Map<String, dynamic>.from(custodia['fkCustodio'] as Map? ?? const {});
            return _asInt(fkCustodio['idCustodio']) ?? _asInt(custodia['idCustodio']);
          })
          .whereType<int>()
          .toSet();
    }).toList();

    if (validosPorEquipo.isEmpty) {
      return <int>{};
    }

    final intersection = Set<int>.from(validosPorEquipo.first);
    for (final current in validosPorEquipo.skip(1)) {
      intersection.removeWhere((id) => !current.contains(id));
    }
    return intersection;
  }

  Future<void> _pickEquipos() async {
    if (_custodioId == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Selecciona primero un custodio.')),
      );
      return;
    }

    final equiposDisponibles = _equiposDelCustodio(_custodioId!);
    final selected = Set<int>.from(_equipoIds);
    final result = await showDialog<Set<int>>(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setModalState) {
            return AlertDialog(
              title: const Text('Seleccionar equipos'),
              content: SizedBox(
                width: double.maxFinite,
                child: ListView(
                  shrinkWrap: true,
                  children: equiposDisponibles.map((item) {
                    final equipoId = item.id;
                    final checked = selected.contains(equipoId);
                    return CheckboxListTile(
                      value: checked,
                      title: Text(_equipoLabel(item)),
                      subtitle: Text(_text(item.serial, fallback: 'Sin serial')),
                      onChanged: (value) {
                        setModalState(() {
                          if (value == true) {
                            selected.add(equipoId);
                          } else {
                            selected.remove(equipoId);
                          }
                        });
                      },
                    );
                  }).toList(),
                ),
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.of(context).pop(),
                  child: const Text('Cancelar'),
                ),
                FilledButton(
                  onPressed: () => Navigator.of(context).pop(selected),
                  child: const Text('Aplicar'),
                ),
              ],
            );
          },
        );
      },
    );

    if (result == null) {
      return;
    }

    setState(() {
      _equipoIds
        ..clear()
        ..addAll(result);
      _ensureValidCustodioSelection();
    });
  }

  String _buildDetalleCompleto() {
    final base = _detalleController.text.trim();
    final observaciones = _observacionControllers.entries
        .map((entry) => MapEntry(entry.key, entry.value.text.trim()))
        .where((entry) => entry.value.isNotEmpty)
        .toList();

    if (observaciones.isEmpty) {
      return base;
    }

    final buffer = StringBuffer(base);
    if (buffer.isNotEmpty) {
      buffer.write('\n\n');
    }
    buffer.writeln('Observaciones por bloque:');
    for (final entry in observaciones) {
      buffer.writeln('- ${entry.key}: ${entry.value}');
    }
    return buffer.toString().trim();
  }

  List<Widget> _buildObservacionesWidgets() {
    return _observacionControllers.entries
        .map(
          (entry) => Padding(
            padding: const EdgeInsets.only(bottom: 12),
            child: TextField(
              controller: entry.value,
              minLines: 2,
              maxLines: 4,
              decoration: InputDecoration(
                labelText: entry.key,
                hintText: 'Observaciones para ${entry.key}',
              ),
            ),
          ),
        )
        .toList();
  }

  void _ensureValidCustodioSelection() {
    final validos = _custodiosValidos();
    if (_custodioId != null && !validos.contains(_custodioId)) {
      _custodioId = null;
      _custodioController.clear();
    } else if (_custodioId != null) {
      _custodioController.text = _custodioDisplay(
        _custodios.firstWhere(
          (item) => (_asInt(item['idCustodio']) ?? _asInt(item['id'])) == _custodioId,
          orElse: () => const <String, dynamic>{},
        ),
      );
    }
  }

  void _onCustodioSelected(Map<String, dynamic> item) {
    final custodioId = _asInt(item['idCustodio']) ?? _asInt(item['id']);
    if (custodioId == null) {
      return;
    }
    final equiposAsignados = _equiposDelCustodio(custodioId).map((equipo) => equipo.id).toSet();
    setState(() {
      _custodioId = custodioId;
      _custodioController.text = _custodioDisplay(item);
      _equipoIds
        ..clear()
        ..addAll(equiposAsignados);
    });
  }

  List<EquipoListItem> _equiposDelCustodio(int custodioId) {
    final equipoIds = _custodias
        .where((custodia) {
          final estado = custodia['estado'] == true;
          final fkCustodio =
              Map<String, dynamic>.from(custodia['fkCustodio'] as Map? ?? const {});
          return estado &&
              ((_asInt(fkCustodio['idCustodio']) == custodioId) ||
                  (_asInt(custodia['idCustodio']) == custodioId));
        })
        .map((custodia) {
          final fkEquipo = Map<String, dynamic>.from(custodia['fkEquipo'] as Map? ?? const {});
          return _asInt(fkEquipo['idEquipo']);
        })
        .whereType<int>()
        .toSet();

    final equipos = _equipos.where((item) => item.id > 0 && equipoIds.contains(item.id)).toList();
    equipos.sort((a, b) => _equipoLabel(a).compareTo(_equipoLabel(b)));
    return equipos;
  }

  Iterable<Map<String, dynamic>> _buscarCustodios(String query) {
    final normalizedQuery = _normalizeText(query);
    final candidatos = _custodios;

    return candidatos.where((item) {
      if (normalizedQuery.isEmpty) {
        return true;
      }
      final nombre = _normalizeText(_text(item['nombre'], fallback: ''));
      final cedula = _normalizeText(_text(item['cedula'], fallback: ''));
      return nombre.contains(normalizedQuery) || cedula.contains(normalizedQuery);
    });
  }

  String _custodioDisplay(Map<String, dynamic> item) {
    final nombre = _text(item['nombre'], fallback: 'Sin nombre');
    final cedula = _text(item['cedula'], fallback: '');
    return cedula.isEmpty ? nombre : '$nombre - $cedula';
  }

  @override
  Widget build(BuildContext context) {
    final canCreate = context.watch<AuthProvider>().hasCapability(
      UserCapability.createMantenimiento,
    );
    if (!canCreate) {
      return Scaffold(
        appBar: AppBar(title: const Text('Nuevo mantenimiento')),
        body: const Center(
          child: Padding(
            padding: EdgeInsets.all(24),
            child: Text(
              'Tu rol no puede crear mantenimientos desde la aplicacion movil.',
              textAlign: TextAlign.center,
            ),
          ),
        ),
      );
    }
    return Scaffold(
      appBar: AppBar(title: const Text('Nuevo mantenimiento')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : Form(
              key: _formKey,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  Autocomplete<Map<String, dynamic>>(
                    initialValue: TextEditingValue(text: _custodioController.text),
                    displayStringForOption: _custodioDisplay,
                    optionsBuilder: (textEditingValue) {
                      return _buscarCustodios(textEditingValue.text).take(20);
                    },
                    onSelected: _onCustodioSelected,
                    fieldViewBuilder: (context, textEditingController, focusNode, onFieldSubmitted) {
                      if (textEditingController.text != _custodioController.text) {
                        textEditingController.value = _custodioController.value;
                      }
                      return TextFormField(
                        controller: textEditingController,
                        focusNode: focusNode,
                        onChanged: (value) {
                          _custodioController.value = textEditingController.value;
                          if (value.trim().isEmpty && _custodioId != null) {
                            setState(() {
                              _custodioId = null;
                              _equipoIds.clear();
                            });
                          }
                        },
                        decoration: const InputDecoration(
                          labelText: 'Custodio',
                          hintText: 'Busca por cédula, nombre o apellidos',
                          prefixIcon: Icon(Icons.search),
                        ),
                        validator: (_) => _custodioId == null ? 'Selecciona un custodio' : null,
                      );
                    },
                    optionsViewBuilder: (context, onSelected, options) {
                      final items = options.toList();
                      return Align(
                        alignment: Alignment.topLeft,
                        child: Material(
                          elevation: 4,
                          borderRadius: BorderRadius.circular(12),
                          child: ConstrainedBox(
                            constraints: const BoxConstraints(maxHeight: 280, minWidth: 280),
                            child: ListView.builder(
                              padding: EdgeInsets.zero,
                              shrinkWrap: true,
                              itemCount: items.length,
                              itemBuilder: (context, index) {
                                final item = items[index];
                                return ListTile(
                                  title: Text(_text(item['nombre'], fallback: 'Sin nombre')),
                                  subtitle: Text(_text(item['cedula'], fallback: 'Sin cédula')),
                                  onTap: () => onSelected(item),
                                );
                              },
                            ),
                          ),
                        ),
                      );
                    },
                  ),
                  const SizedBox(height: 12),
                  OutlinedButton.icon(
                    onPressed: _pickEquipos,
                    icon: const Icon(Icons.devices_outlined),
                    label: Text(
                      _equipoIds.isEmpty
                          ? 'Seleccionar equipos'
                          : 'Equipos seleccionados: ${_equipoIds.length}',
                    ),
                  ),
                  const SizedBox(height: 8),
                  if (_custodioId != null && _equiposDelCustodio(_custodioId!).isEmpty)
                    const Padding(
                      padding: EdgeInsets.only(bottom: 8),
                      child: Text(
                        'El custodio seleccionado no tiene equipos activos asignados.',
                        style: TextStyle(color: Colors.redAccent),
                      ),
                    ),
                  if (_equipoIds.isNotEmpty)
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: _equipos
                          .where((item) => item.id > 0 && _equipoIds.contains(item.id))
                          .map(
                            (item) => InputChip(
                              label: Text(_equipoLabel(item)),
                              onDeleted: () {
                                setState(() {
                                  if (item.id > 0) {
                                    _equipoIds.remove(item.id);
                                  }
                                });
                              },
                            ),
                          )
                          .toList(),
                    ),
                  const SizedBox(height: 12),
                  DropdownButtonFormField<String>(
                    initialValue: _tipo,
                    decoration: const InputDecoration(labelText: 'Tipo'),
                    items: const [
                      DropdownMenuItem(value: 'PREVENTIVO', child: Text('Preventivo')),
                      DropdownMenuItem(value: 'CORRECTIVO', child: Text('Correctivo')),
                    ],
                    onChanged: (value) => setState(() => _tipo = value ?? _tipo),
                  ),
                  const SizedBox(height: 12),
                  DropdownButtonFormField<String>(
                    initialValue: _estadoGeneral,
                    decoration: const InputDecoration(labelText: 'Estado general'),
                    items: const [
                      DropdownMenuItem(value: 'OPERATIVO', child: Text('Operativo')),
                      DropdownMenuItem(value: 'REQUIERE_REVISION', child: Text('Requiere revision')),
                      DropdownMenuItem(value: 'NO_OPERATIVO', child: Text('No operativo')),
                    ],
                    onChanged: (value) =>
                        setState(() => _estadoGeneral = value ?? _estadoGeneral),
                  ),
                  const SizedBox(height: 12),
                  ListTile(
                    contentPadding: EdgeInsets.zero,
                    title: const Text('Fecha de mantenimiento'),
                    subtitle: Text(_formatDate(_fecha)),
                    trailing: const Icon(Icons.calendar_today_outlined),
                    onTap: _selectDate,
                  ),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: _detalleController,
                    minLines: 3,
                    maxLines: 5,
                    decoration: const InputDecoration(
                      labelText: 'Detalle',
                      hintText: 'Describe el trabajo o hallazgo tecnico',
                    ),
                    validator: (value) {
                      if (value == null || value.trim().isEmpty) {
                        return 'Ingresa el detalle del mantenimiento';
                      }
                      return null;
                    },
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'Checklist tecnico',
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 8),
                  if (_actividades.isEmpty)
                    const Card(
                      child: Padding(
                        padding: EdgeInsets.all(16),
                        child: Text('No hay actividades checklist configuradas.'),
                      ),
                    )
                  else
                    ..._buildChecklistWidgets(),
                  const SizedBox(height: 16),
                  Text(
                    'Observaciones por bloque',
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 8),
                  ..._buildObservacionesWidgets(),
                  const SizedBox(height: 16),
                  Text(
                    'Firmas',
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 8),
                  _SignatureCard(
                    title: 'Firma del tecnico',
                    controller: _firmaTecnicoController,
                  ),
                  const SizedBox(height: 12),
                  _SignatureCard(
                    title: 'Firma del custodio',
                    controller: _firmaCustodioController,
                  ),
                  const SizedBox(height: 16),
                  Row(
                    children: [
                      OutlinedButton.icon(
                        onPressed: _pickImages,
                        icon: const Icon(Icons.photo_library_outlined),
                        label: const Text('Agregar imagenes'),
                      ),
                      const SizedBox(width: 12),
                      Text('${_imagenes.length} seleccionadas'),
                    ],
                  ),
                  const SizedBox(height: 12),
                  if (_imagenes.isNotEmpty)
                    ..._imagenes.map(
                      (image) => ListTile(
                        contentPadding: EdgeInsets.zero,
                        leading: const Icon(Icons.image_outlined),
                        title: Text(image.name),
                        subtitle: Text(image.path),
                        trailing: IconButton(
                          onPressed: () {
                            setState(
                              () => _imagenes = _imagenes.where((item) => item.path != image.path).toList(),
                            );
                          },
                          icon: const Icon(Icons.delete_outline),
                        ),
                      ),
                    ),
                  const SizedBox(height: 20),
                  FilledButton.icon(
                    onPressed: _saving ? null : _save,
                    icon: _saving
                        ? const SizedBox(
                            height: 16,
                            width: 16,
                            child: CircularProgressIndicator(strokeWidth: 2),
                          )
                        : const Icon(Icons.save_outlined),
                    label: Text(_saving ? 'Guardando...' : 'Crear mantenimiento'),
                  ),
                ],
              ),
            ),
    );
  }
}

Future<String> _signatureBase64(SignatureController controller) async {
  final bytes = await controller.toPngBytes();
  if (bytes == null || bytes.isEmpty) {
    return '';
  }
  return base64Encode(bytes);
}

class _SignatureCard extends StatelessWidget {
  const _SignatureCard({
    required this.title,
    required this.controller,
  });

  final String title;
  final SignatureController controller;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: const TextStyle(fontWeight: FontWeight.w700),
            ),
            const SizedBox(height: 8),
            Container(
              height: 160,
              decoration: BoxDecoration(
                border: Border.all(color: Colors.black12),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Signature(
                controller: controller,
                backgroundColor: Colors.white,
              ),
            ),
            const SizedBox(height: 8),
            Align(
              alignment: Alignment.centerRight,
              child: TextButton.icon(
                onPressed: controller.clear,
                icon: const Icon(Icons.refresh),
                label: const Text('Limpiar'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

String _equipoLabel(EquipoListItem item) {
  return '${_text(item.codigoSap, fallback: 'Sin codigo')} - ${_text(item.tipoEquipo, fallback: 'Equipo')}';
}

int? _asInt(dynamic value) {
  if (value is int) {
    return value;
  }
  if (value is num) {
    return value.toInt();
  }
  return int.tryParse(value?.toString() ?? '');
}

String _formatDate(DateTime value) {
  final month = value.month.toString().padLeft(2, '0');
  final day = value.day.toString().padLeft(2, '0');
  return '${value.year}-$month-$day';
}

String _normalizeText(String value) {
  const source = 'ÁÀÄÂÉÈËÊÍÌÏÎÓÒÖÔÚÙÜÛÑáàäâéèëêíìïîóòöôúùüûñ';
  const target = 'AAAAEEEEIIIIOOOOUUUUNaaaaeeeeiiiioooouuuun';
  var normalized = value.trim().toLowerCase();
  for (var index = 0; index < source.length; index++) {
    normalized = normalized.replaceAll(source[index].toLowerCase(), target[index].toLowerCase());
  }
  return normalized;
}

String _text(dynamic value, {String fallback = '-'}) {
  final text = value?.toString().trim() ?? '';
  return text.isEmpty ? fallback : text;
}
