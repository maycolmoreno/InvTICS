package com.uisrael.consumogestionactivosapi.controlador;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ActividadPlanificadaRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.CambiarEstadoActividadRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.ActividadPlanificadaResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MetricasCumplimientoResponseDTO;
import com.uisrael.consumogestionactivosapi.security.SesionUsuario;
import com.uisrael.consumogestionactivosapi.service.IActividadPlanificadaServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IUsuariosServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ActividadPlanificadaControlador {

	private final IActividadPlanificadaServicio actividadServicio;
	private final IUsuariosServicio usuariosServicio;
	private final IEquiposServicio equiposServicio;
	private final SesionUsuario sesionUsuario;

	@GetMapping("/planificacion")
	public String vistaPrincipal(Model model) {
		List<ActividadPlanificadaResponseDTO> actividades;

		if (sesionUsuario.tieneRol("TECNICO") && sesionUsuario.getIdUsuario() != null) {
			actividades = actividadServicio.listarPorTecnico(sesionUsuario.getIdUsuario());
		} else {
			actividades = actividadServicio.listarTodas();
		}

		model.addAttribute("actividades", actividades);
		model.addAttribute("tecnicos", usuariosServicio.listarUsuario());
		model.addAttribute("esTecnico", sesionUsuario.tieneRol("TECNICO"));
		return "Planificacion/planificacion";
	}

	@GetMapping("/planificacion/nueva")
	public String formNueva(Model model) {
		model.addAttribute("actividad", new ActividadPlanificadaRequestDTO());
		model.addAttribute("tecnicos", usuariosServicio.listarUsuario());
		model.addAttribute("equipos", equiposServicio.listarEquipos());
		model.addAttribute("esTecnico", sesionUsuario.tieneRol("TECNICO"));
		model.addAttribute("idUsuarioActual", sesionUsuario.getIdUsuario());
		return "Planificacion/planificacion-form";
	}

	@PostMapping("/planificacion/guardar")
	public String guardar(@ModelAttribute ActividadPlanificadaRequestDTO request,
			RedirectAttributes redirectAttributes) {
		try {
			// Asignar el ID del usuario logueado como creador
			if (sesionUsuario.getIdUsuario() != null) {
				request.setCreadoPorId(sesionUsuario.getIdUsuario());
			} else {
				usuariosServicio.listarUsuario().stream()
						.filter(u -> u.getCorreo().equalsIgnoreCase(sesionUsuario.getCorreo())).findFirst()
						.ifPresent(u -> request.setCreadoPorId(u.getIdUsuario()));
			}

			// Si es TECNICO, auto-asignarse como técnico responsable
			if (sesionUsuario.tieneRol("TECNICO") && sesionUsuario.getIdUsuario() != null) {
				request.setTecnicoId(sesionUsuario.getIdUsuario());
			}

			actividadServicio.crear(request);
			redirectAttributes.addFlashAttribute("mensaje", "Actividad creada exitosamente");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/planificacion";
	}

	@PostMapping("/planificacion/{id}/estado")
	public String cambiarEstado(@PathVariable Long id, @ModelAttribute CambiarEstadoActividadRequestDTO request,
			RedirectAttributes redirectAttributes) {
		try {
			actividadServicio.cambiarEstado(id, request);
			redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/planificacion";
	}

	@GetMapping("/planificacion/metricas")
	public String vistaMetricas(@RequestParam(defaultValue = "MENSUAL") String periodo, Model model) {
		List<MetricasCumplimientoResponseDTO> metricas;
		try {
			metricas = actividadServicio.obtenerMetricasGlobales(periodo);
		} catch (Exception e) {
			metricas = List.of();
			model.addAttribute("error", "No se pudieron cargar las métricas: " + e.getMessage());
		}

		// Calcular totales para el resumen global
		long totalCompletadas = metricas.stream().mapToLong(MetricasCumplimientoResponseDTO::getCompletadas).sum();
		long totalPendientes = metricas.stream().mapToLong(MetricasCumplimientoResponseDTO::getPendientes).sum();
		long totalVencidas = metricas.stream().mapToLong(MetricasCumplimientoResponseDTO::getVencidas).sum();
		long totalActividades = metricas.stream().mapToLong(MetricasCumplimientoResponseDTO::getTotalActividades).sum();
		long totalEnProgreso = metricas.stream().mapToLong(MetricasCumplimientoResponseDTO::getEnProgreso).sum();

		model.addAttribute("metricas", metricas);
		model.addAttribute("periodoSeleccionado", periodo);
		model.addAttribute("totalCompletadas", totalCompletadas);
		model.addAttribute("totalPendientes", totalPendientes);
		model.addAttribute("totalVencidas", totalVencidas);
		model.addAttribute("totalActividades", totalActividades);
		model.addAttribute("totalEnProgreso", totalEnProgreso);
		return "Planificacion/metricas";
	}

	// ==================== API endpoints para AJAX ====================

	@GetMapping("/api/planificacion/tecnico/{tecnicoId}")
	@ResponseBody
	public List<ActividadPlanificadaResponseDTO> listarPorTecnico(@PathVariable Integer tecnicoId) {
		return actividadServicio.listarPorTecnico(tecnicoId);
	}

	@GetMapping("/api/planificacion/metricas/tecnico/{tecnicoId}")
	@ResponseBody
	public MetricasCumplimientoResponseDTO metricasTecnico(@PathVariable Integer tecnicoId,
			@RequestParam(defaultValue = "MENSUAL") String periodo) {
		return actividadServicio.obtenerMetricasTecnico(tecnicoId, periodo);
	}

	@GetMapping("/api/planificacion/metricas/global")
	@ResponseBody
	public List<MetricasCumplimientoResponseDTO> metricasGlobal(
			@RequestParam(defaultValue = "MENSUAL") String periodo) {
		return actividadServicio.obtenerMetricasGlobales(periodo);
	}
}
