package com.uisrael.consumogestionactivosapi.controlador;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CategoriaEquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.EquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.MarcasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;import com.uisrael.consumogestionactivosapi.modelo.dto.response.PaginaResponse;import com.uisrael.consumogestionactivosapi.service.EquiposExcelService;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/equipos")
public class EquiposControlador {

	private final IEquiposServicio servicioEquipos;
	private final IMarcasServicio servicioMarcas;
	private final ICategoriaEquiposServicio servicioCategoriaEquipos;
	private final EquiposExcelService equiposExcelService;
	private final IInventarioOperacionServicio inventarioOperacionServicio;

	@GetMapping
	public String listarEquipos(Model model,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		PaginaResponse<EquiposResponseDTO> pagina = servicioEquipos.listarEquiposPaginado(page, size);
		model.addAttribute("listarequipos", pagina.getContenido());
		model.addAttribute("paginaActual", pagina.getPaginaActual());
		model.addAttribute("totalPaginas", pagina.getTotalPaginas());
		model.addAttribute("totalElementos", pagina.getTotalElementos());
		model.addAttribute("tamanioPagina", pagina.getTamanioPagina());
		var bodegas = inventarioOperacionServicio.listarBodegas();
		model.addAttribute("bodegas", bodegas != null ? bodegas : List.of());
		cargarCombos(model, 0, 0);

		return "Equipos/listarEquipos";
	}

	@GetMapping("/mantenimiento")
	public String verMantenimiento() {
		return "redirect:/mantenimiento/nuevo";
	}

	/** El alta/edicion ahora se hace desde un drawer en el listado. */
	@GetMapping("/nuevo-equipo")
	public String nuevoEquipo() {
		return "redirect:/equipos";
	}

	/** El alta/edicion ahora se hace desde un drawer en el listado; se preselecciona el equipo via query param. */
	@GetMapping("/editar-equipo/{id}")
	public String editar(@PathVariable Integer id) {
		return "redirect:/equipos?editarEquipo=" + id;
	}

	@PostMapping
	public String guardarEquipo(@ModelAttribute EquiposRequestDTO equipo, RedirectAttributes redirectAttributes) {

		if (equipo.getFkMarca() == null) {
			equipo.setFkMarca(new MarcasRequestDTO());
			equipo.getFkMarca().setIdMarca(0);
		}
		if (equipo.getFkCategoria() == null) {
			equipo.setFkCategoria(new CategoriaEquiposRequestDTO());
			equipo.getFkCategoria().setIdCategoria(0);
		}

		if (equipo.getModelo() == null || equipo.getModelo().trim().isEmpty()) {
			return error(redirectAttributes, "El modelo es obligatorio");
		}

		if (equipo.getSerial() == null || equipo.getSerial().trim().isEmpty()) {
			return error(redirectAttributes, "El serial es obligatorio");
		}

		if (equipo.getFkMarca().getIdMarca() <= 0) {
			return error(redirectAttributes, "Debe seleccionar una marca");
		}
		if (equipo.getFkCategoria().getIdCategoria() <= 0) {
			return error(redirectAttributes, "Debe seleccionar una categoría");
		}

		if (equipo.getSerial() != null && !equipo.getSerial().isBlank()) {
			boolean serialRepetido = equipo.getIdEquipo() > 0
					? servicioEquipos.existeSerialParaOtro(equipo.getSerial().trim(), equipo.getIdEquipo())
					: servicioEquipos.existeSerial(equipo.getSerial().trim());
			if (serialRepetido) {
				return error(redirectAttributes, "Ya existe un equipo con ese Serial");
			}
		}

		if (equipo.getCodigoSap() != null && !equipo.getCodigoSap().isBlank()) {
			boolean codigoRepetido = equipo.getIdEquipo() > 0
					? servicioEquipos.existeCodigoParaOtro(equipo.getCodigoSap().trim(), equipo.getIdEquipo())
					: servicioEquipos.existeCodigo(equipo.getCodigoSap().trim());
			if (codigoRepetido) {
				return error(redirectAttributes, "Ya existe un equipo con ese Código SAP");
			}
		}

		if (equipo.getMac() != null && !equipo.getMac().isBlank()) {
			if (!esMacValida(equipo.getMac())) {
				return error(redirectAttributes, "La MAC no tiene un formato válido");
			}
			boolean macRepetida = equipo.getIdEquipo() > 0
					? servicioEquipos.existeMACParaOtro(equipo.getMac().trim(), equipo.getIdEquipo())
					: servicioEquipos.existeMAC(equipo.getMac().trim());
			if (macRepetida) {
				return error(redirectAttributes, "Ya existe un equipo con esa dirección MAC");
			}
		}

		if (equipo.getIdEquipo() > 0) {
			servicioEquipos.actualizarEquipo(equipo.getIdEquipo(), equipo);
			redirectAttributes.addFlashAttribute("success", "Equipo actualizado correctamente.");
		} else {
			equipo.setEstado(true);
			servicioEquipos.crearEquipo(equipo);
			redirectAttributes.addFlashAttribute("success", "Equipo creado correctamente.");
		}

		return "redirect:/equipos";
	}

	private String error(RedirectAttributes redirectAttributes, String mensaje) {
		redirectAttributes.addFlashAttribute("error", mensaje);
		return "redirect:/equipos";
	}

	@PostMapping("/eliminar-equipo")
	public String eliminarLogico(@RequestParam Integer idEquipo) {
		servicioEquipos.actualizarEstado(idEquipo, false);
		return "redirect:/equipos";
	}

	@PostMapping("/activar-equipo")
	public String activarEquipo(@RequestParam Integer idEquipo) {
		servicioEquipos.actualizarEstado(idEquipo, true);
		return "redirect:/equipos";
	}

	private void cargarCombos(Model model, int idMarcaSel, int idCatSel) {

		model.addAttribute("listamarcas", servicioMarcas.listarMarca().stream()
				.filter(m -> m.isEstado() || m.getIdMarca() == idMarcaSel).collect(Collectors.toList()));

		model.addAttribute("listacategorias", servicioCategoriaEquipos.listarCategoriaEquipo().stream()
				.filter(c -> c.isEstado() || c.getIdCategoria() == idCatSel).collect(Collectors.toList()));
	}

	public static boolean esMacValida(String mac) {
		if (mac == null || mac.isBlank()) {
			return false;
		}

		String regexMac = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

		return mac.matches(regexMac);
	}

	@GetMapping("/reporte-equipo")
	public String listarEquiposReporte(Model model) {

		List<EquiposResponseDTO> contenidoBD = servicioEquipos.listarEquipos();

		contenidoBD = contenidoBD.stream().sorted(Comparator.comparing(EquiposResponseDTO::getIdEquipo)).toList();

		model.addAttribute("listarequipos", contenidoBD);

		return "Equipos/reporteEquipos";
	}

	@GetMapping("/reporte-equipo/excel")
	public ResponseEntity<byte[]> descargarExcelEquipos() {

		List<EquiposResponseDTO> data = servicioEquipos.listarEquipos();

		byte[] excelBytes = equiposExcelService.generarReporteExcel(data, null);

		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String filename = "reporte_equipos_" + timestamp + ".xlsx";

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType
						.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(excelBytes);
	}

}
