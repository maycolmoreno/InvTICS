package com.uisrael.consumogestionactivosapi.controlador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoManualResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MetricasCumplimientoResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IActividadPlanificadaServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoManualServicio;
import com.uisrael.consumogestionactivosapi.service.IUsuariosServicio;
import com.uisrael.consumogestionactivosapi.service.MantenimientosExcelService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reportes")
public class ReportesControlador {

	private final IEquiposServicio equiposServicio;
	private final IMantenimientoManualServicio mantenimientoServicio;
	private final ICustodiasServicio custodiasServicio;
	private final ICustodiosServicio custodiosServicio;
	private final IUsuariosServicio usuariosServicio;
	private final IActividadPlanificadaServicio actividadPlanificadaServicio;
	private final MantenimientosExcelService mantenimientosExcelService;

	// ── Centro de Reportes ──────────────────────────────────

	@GetMapping
	public String centroReportes(Model model) {
		List<EquiposResponseDTO> equipos = equiposServicio.listarEquipos();
		List<MantenimientoManualResponseDTO> mantenimientos = mantenimientoServicio.listarTodos();
		List<CustodiasResponseDTO> custodias = custodiasServicio.listarCustodias();

		// ── KPIs generales ──
		model.addAttribute("totalEquipos", equipos.size());
		model.addAttribute("equiposActivos", equipos.stream().filter(EquiposResponseDTO::isEstado).count());
		model.addAttribute("totalMantenimientos", mantenimientos.size());
		model.addAttribute("mantenimientosAbiertos", mantenimientos.stream()
				.filter(m -> !contiene(m.getEstadoInterno(), "cerrad", "complet", "finaliz")).count());
		model.addAttribute("totalCustodias", custodias.size());
		model.addAttribute("totalCustodios", custodiosServicio.listarCustodios().size());

		// ── Equipos por categoría (para gráfico pie) ──
		Map<String, Long> equiposPorCategoria = equipos.stream()
				.collect(Collectors.groupingBy(
						e -> e.getFkCategoria() != null ? e.getFkCategoria().getNombre() : "Sin categoría",
						Collectors.counting()));
		model.addAttribute("categoriasLabels", equiposPorCategoria.keySet());
		model.addAttribute("categoriasValores", equiposPorCategoria.values());

		// ── Equipos por marca (para gráfico barras) ──
		Map<String, Long> equiposPorMarca = equipos.stream()
				.collect(Collectors.groupingBy(
						e -> e.getFkMarca() != null ? e.getFkMarca().getNombre() : "Sin marca",
						Collectors.counting()));
		equiposPorMarca = equiposPorMarca.entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed())
				.limit(10)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
		model.addAttribute("marcasLabels", equiposPorMarca.keySet());
		model.addAttribute("marcasValores", equiposPorMarca.values());

		// ── Equipos por estado ──
		Map<String, Long> equiposPorEstado = equipos.stream()
				.collect(Collectors.groupingBy(
						e -> e.getEstadoEquipo() != null ? e.getEstadoEquipo() : "Sin estado",
						Collectors.counting()));
		model.addAttribute("estadosLabels", equiposPorEstado.keySet());
		model.addAttribute("estadosValores", equiposPorEstado.values());

		// ── Mantenimientos por tipo ──
		Map<String, Long> mantenimientosPorTipo = mantenimientos.stream()
				.collect(Collectors.groupingBy(
						m -> m.getTipoMantenimiento() != null ? m.getTipoMantenimiento() : "Sin tipo",
						Collectors.counting()));
		model.addAttribute("mntTipoLabels", mantenimientosPorTipo.keySet());
		model.addAttribute("mntTipoValores", mantenimientosPorTipo.values());

		// ── Mantenimientos por estado interno ──
		Map<String, Long> mantenimientosPorEstado = mantenimientos.stream()
				.collect(Collectors.groupingBy(
						m -> m.getEstadoInterno() != null ? m.getEstadoInterno() : "Sin estado",
						Collectors.counting()));
		model.addAttribute("mntEstadoLabels", mantenimientosPorEstado.keySet());
		model.addAttribute("mntEstadoValores", mantenimientosPorEstado.values());

		// ── Mantenimientos por técnico (top 10) ──
		Map<String, Long> mantenimientosPorTecnico = mantenimientos.stream()
				.collect(Collectors.groupingBy(
						m -> m.getTecnicoNombre() != null ? m.getTecnicoNombre() : "Sin asignar",
						Collectors.counting()));
		mantenimientosPorTecnico = mantenimientosPorTecnico.entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed())
				.limit(10)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
		model.addAttribute("mntTecnicoLabels", mantenimientosPorTecnico.keySet());
		model.addAttribute("mntTecnicoValores", mantenimientosPorTecnico.values());

		// ── Mantenimientos por mes (últimos 6 meses) ──
		LocalDate hace6Meses = LocalDate.now().minusMonths(6).withDayOfMonth(1);
		Map<String, Long> mantenimientosPorMes = mantenimientos.stream()
				.filter(m -> m.getFechaMantenimiento() != null && !m.getFechaMantenimiento().isBefore(hace6Meses))
				.collect(Collectors.groupingBy(
						m -> m.getFechaMantenimiento().format(DateTimeFormatter.ofPattern("yyyy-MM")),
						Collectors.counting()));
		mantenimientosPorMes = mantenimientosPorMes.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
		model.addAttribute("mntMesLabels", mantenimientosPorMes.keySet());
		model.addAttribute("mntMesValores", mantenimientosPorMes.values());

		// ── Métricas de cumplimiento (actividades planificadas) ──
		try {
			List<MetricasCumplimientoResponseDTO> metricas = actividadPlanificadaServicio
					.obtenerMetricasGlobales("MENSUAL");
			model.addAttribute("metricasTecnicos", metricas);
		} catch (Exception e) {
			model.addAttribute("metricasTecnicos", List.of());
		}

		return "reportes/centro-reportes";
	}

	// ── Reporte de Mantenimientos ───────────────────────────

	@GetMapping("/mantenimientos")
	public String reporteMantenimientos(
			@RequestParam(required = false) String estado,
			@RequestParam(required = false) String tipo,
			@RequestParam(required = false) Integer tecnicoId,
			Model model) {

		List<MantenimientoManualResponseDTO> data = mantenimientoServicio.listarTodos();

		// Listas de filtros
		List<String> listaEstados = data.stream()
				.map(MantenimientoManualResponseDTO::getEstadoInterno)
				.filter(e -> e != null && !e.isBlank())
				.distinct().sorted().toList();
		List<String> listaTipos = data.stream()
				.map(MantenimientoManualResponseDTO::getTipoMantenimiento)
				.filter(t -> t != null && !t.isBlank())
				.distinct().sorted().toList();

		// Filtrar
		if (estado != null && !estado.isBlank()) {
			data = data.stream().filter(m -> estado.equalsIgnoreCase(m.getEstadoInterno())).toList();
		}
		if (tipo != null && !tipo.isBlank()) {
			data = data.stream().filter(m -> tipo.equalsIgnoreCase(m.getTipoMantenimiento())).toList();
		}
		if (tecnicoId != null && tecnicoId > 0) {
			data = data.stream().filter(m -> tecnicoId.equals(m.getTecnicoId())).toList();
		}

		data = data.stream()
				.sorted(Comparator.comparing(MantenimientoManualResponseDTO::getCreadoEn,
						Comparator.nullsLast(Comparator.reverseOrder())))
				.toList();

		model.addAttribute("listaMantenimientos", data);
		model.addAttribute("listaEstados", listaEstados);
		model.addAttribute("listaTipos", listaTipos);
		model.addAttribute("listaUsuarios", usuariosServicio.listarUsuario());
		model.addAttribute("estadoSeleccionado", estado);
		model.addAttribute("tipoSeleccionado", tipo);
		model.addAttribute("tecnicoSeleccionado", tecnicoId);

		return "reportes/reporte-mantenimientos";
	}

	@GetMapping("/mantenimientos/excel")
	public ResponseEntity<byte[]> descargarExcelMantenimientos(
			@RequestParam(required = false) String estado,
			@RequestParam(required = false) String tipo,
			@RequestParam(required = false) Integer tecnicoId) {

		List<MantenimientoManualResponseDTO> data = mantenimientoServicio.listarTodos();

		if (estado != null && !estado.isBlank()) {
			data = data.stream().filter(m -> estado.equalsIgnoreCase(m.getEstadoInterno())).toList();
		}
		if (tipo != null && !tipo.isBlank()) {
			data = data.stream().filter(m -> tipo.equalsIgnoreCase(m.getTipoMantenimiento())).toList();
		}
		if (tecnicoId != null && tecnicoId > 0) {
			data = data.stream().filter(m -> tecnicoId.equals(m.getTecnicoId())).toList();
		}

		byte[] bytes = mantenimientosExcelService.generarReporteExcel(data, estado, tipo);

		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String filename = "reporte_mantenimientos_" + timestamp + ".xlsx";

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType(
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(bytes);
	}

	private boolean contiene(String value, String... terms) {
		if (value == null)
			return false;
		String normalized = value.toLowerCase(Locale.ROOT);
		for (String term : terms) {
			if (normalized.contains(term))
				return true;
		}
		return false;
	}
}
