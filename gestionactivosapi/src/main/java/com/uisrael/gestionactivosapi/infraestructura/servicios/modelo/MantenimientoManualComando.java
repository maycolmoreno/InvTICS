package com.uisrael.gestionactivosapi.infraestructura.servicios.modelo;

import java.time.LocalDate;
import java.util.List;

public record MantenimientoManualComando(
		List<Integer> equipoIds,
		Integer custodioId,
		String tipoMantenimiento,
		LocalDate fechaMantenimiento,
		String detalle,
		String estadoGeneral,
		LocalDate proximaFecha,
		String firmaTecnico,
		String firmaCustodio,
		String ipOrigen,
		List<ActividadManualComando> actividades,
		List<ImagenMantenimientoComando> imagenes,
		Integer idProgramado) {
}
