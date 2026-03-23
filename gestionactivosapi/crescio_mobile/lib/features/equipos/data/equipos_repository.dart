import '../../../core/network/api_client.dart';
import 'equipo_models.dart';

class EquiposRepository {
  const EquiposRepository(this._apiClient);

  final ApiClient _apiClient;

  Future<List<EquipoListItem>> listar() async {
    final data = await _apiClient.get('/equipos');
    final items = (data as List)
        .map((item) => EquipoListItem.fromJson(Map<String, dynamic>.from(item as Map)))
        .toList();
    items.sort((a, b) => a.codigoSap.compareTo(b.codigoSap));
    return items;
  }

  Future<EquipoHistorial> obtenerDetalle(int equipoId) async {
    final data = await _apiClient.get('/historial/$equipoId');
    return EquipoHistorial.fromJson(Map<String, dynamic>.from(data as Map));
  }

  Future<List<EquipoListItem>> listarConHistorial() async {
    final equipos = await listar();
    final enriched = <EquipoListItem>[];
    for (final equipo in equipos) {
      final equipoId = equipo.id;
      if (equipoId == 0) {
        enriched.add(equipo);
        continue;
      }
      try {
        final historial = await obtenerDetalle(equipoId);
        enriched.add(
          EquipoListItem(
            id: equipo.id,
            codigoSap: equipo.codigoSap,
            tipoEquipo: equipo.tipoEquipo,
            modelo: equipo.modelo,
            serial: equipo.serial,
            estadoEquipo: equipo.estadoEquipo,
            procesador: equipo.procesador,
            ip: equipo.ip,
            mac: equipo.mac,
            custodioNombre: historial.equipo.custodioNombre.isEmpty
                ? equipo.custodioNombre
                : historial.equipo.custodioNombre,
            ubicacionNombre: historial.equipo.ubicacionNombre.isEmpty
                ? equipo.ubicacionNombre
                : historial.equipo.ubicacionNombre,
            estadoMantenimiento: historial.estadoMantenimiento,
            diasSinMantenimiento: historial.estadisticas.diasSinMantenimiento,
          ),
        );
      } catch (_) {
        enriched.add(equipo);
      }
    }
    enriched.sort(_priorityCompare);
    return enriched;
  }
}

int _priorityCompare(EquipoListItem a, EquipoListItem b) {
  final rankCompare = _priorityRank(a).compareTo(_priorityRank(b));
  if (rankCompare != 0) {
    return rankCompare;
  }
  final diasA = a.diasSinMantenimiento;
  final diasB = b.diasSinMantenimiento;
  if (diasA != diasB) {
    return diasB.compareTo(diasA);
  }
  return a.codigoSap.compareTo(b.codigoSap);
}

int _priorityRank(EquipoListItem item) {
  final estadoEquipo = item.estadoEquipo.toUpperCase();
  final estadoMantenimiento = item.estadoMantenimiento.toUpperCase();
  final dias = item.diasSinMantenimiento;

  if (estadoEquipo == 'NO_OPERATIVO' || estadoEquipo == 'BAJA') {
    return 0;
  }
  if (estadoMantenimiento.contains('VENC') || dias >= 180) {
    return 1;
  }
  if (estadoMantenimiento.contains('PROX') || dias >= 90) {
    return 2;
  }
  if (estadoEquipo == 'REQUIERE_REVISION') {
    return 3;
  }
  return 4;
}
