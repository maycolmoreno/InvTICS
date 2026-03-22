package com.uisrael.consumogestionactivosapi.controlador;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoManualResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoManualServicio;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InicioControlador {

	private final IEquiposServicio equiposServicio;
	private final IMantenimientoManualServicio mantenimientoManualServicio;
	private final IUbicacionesServicio ubicacionesServicio;

	@GetMapping("/inicio")
	public String mostrarInicio(Model model) {
		List<EquiposResponseDTO> equipos = equiposServicio.listarEquipos();
		List<MantenimientoManualResponseDTO> mantenimientos = mantenimientoManualServicio.listarTodos();
		List<MantenimientoManualResponseDTO> recientes = mantenimientos.stream()
				.sorted(Comparator.comparing(MantenimientoManualResponseDTO::getCreadoEn,
						Comparator.nullsLast(Comparator.reverseOrder()))
						.thenComparing(MantenimientoManualResponseDTO::getFechaMantenimiento,
								Comparator.nullsLast(Comparator.reverseOrder())))
				.limit(4)
				.toList();
		List<MantenimientoManualResponseDTO> proximos = mantenimientos.stream()
				.filter(m -> m.getProximaFecha() != null && !m.getProximaFecha().isBefore(LocalDate.now()))
				.sorted(Comparator.comparing(MantenimientoManualResponseDTO::getProximaFecha))
				.limit(4)
				.toList();

		model.addAttribute("totalEquipos", equipos.size());
		model.addAttribute("equiposActivos", equipos.stream().filter(EquiposResponseDTO::isEstado).count());
		model.addAttribute("equiposNuevos",
				equipos.stream().filter(e -> contains(e.getEstadoEquipo(), "nuevo", "stock")).count());
		model.addAttribute("equiposConNovedad", equipos.stream()
				.filter(e -> contains(e.getEstadoEquipo(), "mantenimiento", "repar", "falla", "novedad", "proceso"))
				.count());
		model.addAttribute("totalUbicaciones", ubicacionesServicio.listarUbicaciones().size());
		model.addAttribute("mantenimientosActivos", mantenimientos.stream()
				.filter(m -> !contains(m.getEstadoInterno(), "complet", "finaliz", "cerrad"))
				.count());
		model.addAttribute("mantenimientosVencidos", mantenimientos.stream()
				.filter(m -> m.getProximaFecha() != null && m.getProximaFecha().isBefore(LocalDate.now()))
				.filter(m -> !contains(m.getEstadoInterno(), "complet", "finaliz", "cerrad"))
				.count());
		model.addAttribute("mantenimientosRecientes", recientes);
		model.addAttribute("mantenimientosProximos", proximos);
		return "inicio";
	}

	private boolean contains(String value, String... terms) {
		if (value == null) {
			return false;
		}
		String normalized = value.toLowerCase(Locale.ROOT);
		for (String term : terms) {
			if (normalized.contains(term)) {
				return true;
			}
		}
		return false;
	}
}
