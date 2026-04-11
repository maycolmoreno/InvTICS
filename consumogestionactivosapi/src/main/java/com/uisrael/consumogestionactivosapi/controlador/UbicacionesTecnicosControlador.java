package com.uisrael.consumogestionactivosapi.controlador;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ConsentimientoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.HistorialGpsResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionActivaResponseDTO;
import com.uisrael.consumogestionactivosapi.security.SesionUsuario;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesTecnicosServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ubicaciones-tecnicos")
public class UbicacionesTecnicosControlador {

	private final IUbicacionesTecnicosServicio servicio;
	private final SesionUsuario sesionUsuario;

	/**
	 * Pantalla de consentimiento GPS (TECNICO)
	 */
	@GetMapping("/consentimiento")
	public String verConsentimiento(Model model) {
		model.addAttribute("tecnicoId", sesionUsuario.getIdUsuario());
		return "ubicacionesTecnicos/consentimiento";
	}

	/**
	 * Procesar aceptación de consentimiento (TECNICO)
	 */
	@PostMapping("/consentimiento")
	public String registrarConsentimiento(RedirectAttributes redirectAttributes) {
		try {
			ConsentimientoRequestDTO dto = new ConsentimientoRequestDTO();
			dto.setTecnicoId(sesionUsuario.getIdUsuario());
			dto.setVersionTerminos("1.0");
			servicio.registrarConsentimiento(dto);
			redirectAttributes.addFlashAttribute("exito", "Consentimiento de monitoreo GPS registrado correctamente.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error al registrar consentimiento: " + e.getMessage());
		}
		return "redirect:/ubicaciones-tecnicos/consentimiento";
	}

	/**
	 * Pantalla de ubicaciones en tiempo real (ADMINISTRADOR)
	 */
	@GetMapping("/tiempo-real")
	public String verTiempoReal(Model model) {
		try {
			List<UbicacionActivaResponseDTO> ubicaciones = servicio.obtenerUbicacionesTiempoReal();
			model.addAttribute("ubicaciones", ubicaciones);
		} catch (Exception e) {
			model.addAttribute("error", "Error al cargar ubicaciones: " + e.getMessage());
		}
		return "ubicacionesTecnicos/tiempoReal";
	}

	/**
	 * Pantalla de historial GPS por fecha (ADMINISTRADOR)
	 */
	@GetMapping("/historial")
	public String verHistorial(
			@RequestParam(value = "fecha", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
			Model model) {
		if (fecha == null) {
			fecha = LocalDate.now();
		}
		model.addAttribute("fechaSeleccionada", fecha);
		try {
			List<HistorialGpsResponseDTO> historial = servicio.obtenerHistorialGps(fecha);
			model.addAttribute("historial", historial);
		} catch (Exception e) {
			model.addAttribute("error", "Error al cargar historial GPS: " + e.getMessage());
			model.addAttribute("historial", Collections.emptyList());
		}
		return "ubicacionesTecnicos/historial";
	}
}
