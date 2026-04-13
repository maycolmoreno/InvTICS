package com.uisrael.consumogestionactivosapi.controlador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventario")
public class InventarioControlador {

	private final ICustodiasServicio servicioCustodias;

	@GetMapping("/por-sucursal")
	public String inventarioPorSucursal(Model model) {
		List<CustodiasResponseDTO> activas = obtenerCustodiasActivas();

		Map<String, List<CustodiasResponseDTO>> agrupado = activas.stream()
				.collect(Collectors.groupingBy(this::extraerSucursal,
						LinkedHashMap::new, Collectors.toList()));

		Map<String, Long> conteo = new LinkedHashMap<>();
		agrupado.forEach((sucursal, lista) -> conteo.put(sucursal,
				lista.stream().map(c -> c.getFkEquipo().getIdEquipo()).distinct().count()));

		model.addAttribute("agrupado", agrupado);
		model.addAttribute("conteo", conteo);
		model.addAttribute("totalEquipos", activas.stream()
				.map(c -> c.getFkEquipo().getIdEquipo()).distinct().count());
		return "Inventario/porSucursal";
	}

	@GetMapping("/por-departamento")
	public String inventarioPorDepartamento(Model model) {
		List<CustodiasResponseDTO> activas = obtenerCustodiasActivas();

		Map<String, List<CustodiasResponseDTO>> agrupado = activas.stream()
				.collect(Collectors.groupingBy(this::extraerDepartamento,
						LinkedHashMap::new, Collectors.toList()));

		Map<String, Long> conteo = new LinkedHashMap<>();
		agrupado.forEach((depto, lista) -> conteo.put(depto,
				lista.stream().map(c -> c.getFkEquipo().getIdEquipo()).distinct().count()));

		model.addAttribute("agrupado", agrupado);
		model.addAttribute("conteo", conteo);
		model.addAttribute("totalEquipos", activas.stream()
				.map(c -> c.getFkEquipo().getIdEquipo()).distinct().count());
		return "Inventario/porDepartamento";
	}

	@GetMapping("/por-custodio")
	public String inventarioPorCustodio(Model model) {
		List<CustodiasResponseDTO> activas = obtenerCustodiasActivas();

		Map<String, List<CustodiasResponseDTO>> agrupado = activas.stream()
				.collect(Collectors.groupingBy(this::extraerCustodio,
						LinkedHashMap::new, Collectors.toList()));

		Map<String, Long> conteo = new LinkedHashMap<>();
		agrupado.forEach((custodio, lista) -> conteo.put(custodio,
				lista.stream().map(c -> c.getFkEquipo().getIdEquipo()).distinct().count()));

		model.addAttribute("agrupado", agrupado);
		model.addAttribute("conteo", conteo);
		model.addAttribute("totalEquipos", activas.stream()
				.map(c -> c.getFkEquipo().getIdEquipo()).distinct().count());
		return "Inventario/porCustodio";
	}

	private List<CustodiasResponseDTO> obtenerCustodiasActivas() {
		List<CustodiasResponseDTO> todas = servicioCustodias.listarCustodias();
		if (todas == null) return new ArrayList<>();
		return todas.stream()
				.filter(c -> c.isEstado() && c.getFkEquipo() != null)
				.toList();
	}

	private String extraerSucursal(CustodiasResponseDTO c) {
		if (c.getFkCustodio() != null && c.getFkCustodio().getFkUbicacion() != null) {
			String nombre = c.getFkCustodio().getFkUbicacion().getNombre();
			if (nombre != null && !nombre.isBlank()) return nombre;
		}
		return "Sin sucursal";
	}

	private String extraerDepartamento(CustodiasResponseDTO c) {
		if (c.getFkCustodio() != null && c.getFkCustodio().getFkDepartamento() != null) {
			String nombre = c.getFkCustodio().getFkDepartamento().getNombre();
			if (nombre != null && !nombre.isBlank()) return nombre;
		}
		return "Sin departamento";
	}

	private String extraerCustodio(CustodiasResponseDTO c) {
		if (c.getFkCustodio() != null) {
			String nombre = c.getFkCustodio().getNombre();
			if (nombre != null && !nombre.isBlank()) return nombre;
		}
		return "Sin custodio";
	}
}
