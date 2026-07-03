package com.uisrael.consumogestionactivosapi.controlador;

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

import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoManualResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MetricasCumplimientoResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IActividadPlanificadaServicio;
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
	private final IUsuariosServicio usuariosServicio;
	private final IActividadPlanificadaServicio actividadPlanificadaServicio;
	private final MantenimientosExcelService mantenimientosExcelService;

	// ── Centro de Reportes ──────────────────────────────────

	private static final List<String> PERIODOS_METRICAS = List.of("SEMANAL", "MENSUAL", "GLOBAL");
	private static final int MAX_CATEGORIAS_GRAFICO = 5;

	@GetMapping
	public String centroReportes(@RequestParam(defaultValue = "MENSUAL") String periodo, Model model) {
		List<EquiposResponseDTO> equipos = equiposServicio.listarEquipos();

		// ── Equipos por categoría: top 5 y el resto agrupado en "Otros" ──
		Map<String, Long> equiposPorCategoria = equipos.stream()
				.collect(Collectors.groupingBy(
						e -> e.getFkCategoria() != null ? e.getFkCategoria().getNombre() : "Sin categoría",
						Collectors.counting()));
		Map<String, Long> categoriasTop = equiposPorCategoria.entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed())
				.limit(MAX_CATEGORIAS_GRAFICO)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
		long otros = equiposPorCategoria.values().stream().mapToLong(Long::longValue).sum()
				- categoriasTop.values().stream().mapToLong(Long::longValue).sum();
		if (otros > 0) {
			categoriasTop.put("Otros", otros);
		}
		model.addAttribute("categoriasLabels", categoriasTop.keySet());
		model.addAttribute("categoriasValores", categoriasTop.values());

		// ── Equipos por marca (top 10, serie única) ──
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

		// ── Equipos por estado (colores semánticos en el cliente) ──
		Map<String, Long> equiposPorEstado = equipos.stream()
				.collect(Collectors.groupingBy(
						e -> e.getEstadoEquipo() != null ? e.getEstadoEquipo() : "Sin estado",
						Collectors.counting()));
		model.addAttribute("estadosLabels", equiposPorEstado.keySet());
		model.addAttribute("estadosValores", equiposPorEstado.values());

		// ── Métricas de cumplimiento (actividades planificadas por técnico) ──
		String periodoNormalizado = PERIODOS_METRICAS.contains(periodo.toUpperCase(Locale.ROOT))
				? periodo.toUpperCase(Locale.ROOT)
				: "MENSUAL";
		List<MetricasCumplimientoResponseDTO> metricas = List.of();
		boolean metricasError = false;
		try {
			metricas = actividadPlanificadaServicio.obtenerMetricasGlobales(periodoNormalizado);
		} catch (Exception e) {
			metricasError = true;
		}
		// El backend calcula métricas solo para usuarios con rol TECNICO: si no
		// hay técnicos la lista llega vacía y el empty state debe explicarlo.
		boolean hayTecnicos = !metricas.isEmpty() || usuariosServicio.listarUsuario().stream()
				.anyMatch(u -> u.getFkRol() != null && u.getFkRol().getNombre() != null
						&& u.getFkRol().getNombre().toUpperCase(Locale.ROOT).contains("TECNICO"));
		model.addAttribute("metricasTecnicos", metricas);
		model.addAttribute("metricasError", metricasError);
		model.addAttribute("hayTecnicos", hayTecnicos);
		model.addAttribute("periodoSeleccionado", periodoNormalizado);

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

}
