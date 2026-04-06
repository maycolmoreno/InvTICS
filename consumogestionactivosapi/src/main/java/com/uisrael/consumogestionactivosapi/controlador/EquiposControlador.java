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

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CategoriaEquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.EquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.MarcasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.UbicacionesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;import com.uisrael.consumogestionactivosapi.modelo.dto.response.PaginaResponse;import com.uisrael.consumogestionactivosapi.service.EquiposExcelService;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/equipos")
public class EquiposControlador {

	private final IEquiposServicio servicioEquipos;
	private final IMarcasServicio servicioMarcas;
	private final ICategoriaEquiposServicio servicioCategoriaEquipos;
	private final IUbicacionesServicio servicioUbicaciones;
	private final EquiposExcelService equiposExcelService;

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

		return "Equipos/listarEquipos";
	}

	@GetMapping("/mantenimiento")
	public String verMantenimiento() {
		return "redirect:/mantenimiento/nuevo";
	}

	@GetMapping("/nuevo-equipo")
	public String nuevoEquipo(Model model) {

		EquiposRequestDTO equipo = new EquiposRequestDTO();
		equipo.setEstado(true);

		equipo.setFkMarca(new MarcasRequestDTO());
		equipo.getFkMarca().setIdMarca(0);

		equipo.setFkCategoria(new CategoriaEquiposRequestDTO());
		equipo.getFkCategoria().setIdCategoria(0);

		equipo.setFkUbicacion(new UbicacionesRequestDTO());
		equipo.getFkUbicacion().setIdUbicacion(0);

		// combos
		cargarCombos(model, 0, 0);
		model.addAttribute("equipo", equipo);
		return "Equipos/nuevoEquipo";
	}

	@GetMapping("/editar-equipo/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		EquiposResponseDTO dto = servicioEquipos.obtenerPorId(id);

		Integer idMarca = dto.getFkMarca().getIdMarca();

		Integer idCategoria = dto.getFkCategoria().getIdCategoria();

		// Inicializar fkUbicacion si viene null para evitar NPE en el template
		if (dto.getFkUbicacion() == null) {
			var ub = new com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionesResponseDTO();
			ub.setIdUbicacion(0);
			dto.setFkUbicacion(ub);
		}

		model.addAttribute("listamarcas", servicioMarcas.listarMarca().stream()
				.filter(marca -> marca.isEstado() || marca.getIdMarca() == idMarca).toList());

		model.addAttribute("listacategorias", servicioCategoriaEquipos.listarCategoriaEquipo().stream()
				.filter(cate -> cate.isEstado() || cate.getIdCategoria() == idCategoria).toList());

		model.addAttribute("listaubicaciones",
				servicioUbicaciones.listarUbicaciones().stream().filter(u -> u.isEstado()).toList());

		model.addAttribute("equipo", dto);

		return "Equipos/editarEquipo";
	}

	@PostMapping
	public String guardarEquipo(@ModelAttribute EquiposRequestDTO equipo, Model model) {

		if (equipo.getFkMarca() == null) {
			equipo.setFkMarca(new MarcasRequestDTO());
			equipo.getFkMarca().setIdMarca(0);
		}
		if (equipo.getFkCategoria() == null) {
			equipo.setFkCategoria(new CategoriaEquiposRequestDTO());
			equipo.getFkCategoria().setIdCategoria(0);
		}
		if (equipo.getFkUbicacion() == null) {
			equipo.setFkUbicacion(new UbicacionesRequestDTO());
			equipo.getFkUbicacion().setIdUbicacion(0);
		}

		boolean hayErrores = false;

		if (equipo.getTipoEquipo() == null || equipo.getTipoEquipo().trim().isEmpty()) {
			model.addAttribute("errorTipoEquipo", "El tipo de equipo es obligatorio");
			hayErrores = true;
		}

		if (equipo.getModelo() == null || equipo.getModelo().trim().isEmpty()) {
			model.addAttribute("errorModelo", "El modelo es obligatorio");
			hayErrores = true;
		}

		if (equipo.getSerial() == null || equipo.getSerial().trim().isEmpty()) {
			model.addAttribute("errorSerial", "El serial es obligatorio");
			hayErrores = true;
		}

		if (equipo.getFkMarca().getIdMarca() <= 0) {
			model.addAttribute("errorMarca", "Debe seleccionar una marca");
			hayErrores = true;
		}
		if (equipo.getFkCategoria().getIdCategoria() <= 0) {
			model.addAttribute("errorCategoria", "Debe seleccionar una categoría");
			hayErrores = true;
		}

		boolean ipRepetida;
		if (equipo.getIp() != null && !equipo.getIp().isBlank()) {

			if (equipo.getIdEquipo() > 0) {
				// edición
				ipRepetida = servicioEquipos.existeIPParaOtro(equipo.getIp().trim(), equipo.getIdEquipo());
			} else {
				// creación
				ipRepetida = servicioEquipos.existeIP(equipo.getIp().trim());
			}

			if (ipRepetida) {
				model.addAttribute("errorIp", "Ya existe un equipo con esa dirección IP");
				hayErrores = true;
			}

			if (!esIpValida(equipo.getIp())) {
				model.addAttribute("errorIp", "La IP no tiene un formato válido");
				hayErrores = true;
			}
		}

		boolean serialRepetido;
		if (equipo.getSerial() != null && !equipo.getSerial().isBlank()) {

			if (equipo.getIdEquipo() > 0) {
				// edición
				serialRepetido = servicioEquipos.existeSerialParaOtro(equipo.getSerial().trim(), equipo.getIdEquipo());
			} else {
				// creación
				serialRepetido = servicioEquipos.existeSerial(equipo.getSerial().trim());
			}

			if (serialRepetido) {
				model.addAttribute("errorSerial", "Ya existe un equipo con ese Serial");
				hayErrores = true;
			}
		}

		boolean codigoRepetido;
		if (equipo.getCodigoSap() != null && !equipo.getCodigoSap().isBlank()) {

			if (equipo.getIdEquipo() > 0) {
				// edición
				codigoRepetido = servicioEquipos.existeCodigoParaOtro(equipo.getCodigoSap().trim(),
						equipo.getIdEquipo());
			} else {
				// creación
				codigoRepetido = servicioEquipos.existeCodigo(equipo.getCodigoSap().trim());
			}

			if (codigoRepetido) {
				model.addAttribute("errorCodigo", "Ya existe un equipo con ese Código SAP");
				hayErrores = true;
			}
		}

		boolean macRepetida;
		if (equipo.getMac() != null && !equipo.getMac().isBlank()) {

			if (equipo.getIdEquipo() > 0) {
				// edición
				macRepetida = servicioEquipos.existeMACParaOtro(equipo.getMac().trim(), equipo.getIdEquipo());
			} else {
				// creación
				macRepetida = servicioEquipos.existeMAC(equipo.getMac().trim());
			}

			if (macRepetida) {
				model.addAttribute("errorMac", "Ya existe un equipo con esa dirección MAC");
				hayErrores = true;
			}

			if (!esMacValida(equipo.getMac())) {
				model.addAttribute("errorMac", "La MAC no tiene un formato válido");
				hayErrores = true;
			}
		}

		if (hayErrores) {
			int idMarca = equipo.getFkMarca().getIdMarca();
			int idCat = equipo.getFkCategoria().getIdCategoria();

			cargarCombos(model, idMarca, idCat);
			model.addAttribute("equipo", equipo);
			return ubicacionesFormulario(equipo); // solo nuevo (si luego haces editar, se ajusta)
		}

		// Si ubicacion no fue seleccionada, enviar null al API
		if (equipo.getFkUbicacion() != null && equipo.getFkUbicacion().getIdUbicacion() <= 0) {
			equipo.setFkUbicacion(null);
		}

		if (equipo.getIdEquipo() > 0) {
			servicioEquipos.actualizarEquipo(equipo.getIdEquipo(), equipo);
		} else {
			equipo.setEstado(true);
			servicioEquipos.crearEquipo(equipo);
		}

		return "redirect:/equipos";
	}

	private String ubicacionesFormulario(EquiposRequestDTO equipo) {
		return (equipo.getIdEquipo() > 0) ? "Equipos/editarEquipo" : "Equipos/nuevoEquipo";
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

		model.addAttribute("listaubicaciones", servicioUbicaciones.listarUbicaciones().stream()
				.filter(u -> u.isEstado()).collect(Collectors.toList()));
	}

	public static boolean esIpValida(String ip) {
		if (ip == null || ip.isBlank()) {
			return false;
		}

		String regexIp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}" + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

		return ip.matches(regexIp);
	}

	public static boolean esMacValida(String mac) {
		if (mac == null || mac.isBlank()) {
			return false;
		}

		String regexMac = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

		return mac.matches(regexMac);
	}

	@GetMapping("/reporte-equipo")
	public String listarEquiposReporte(@RequestParam(required = false) String tipo, Model model) {

		List<EquiposResponseDTO> contenidoBD = servicioEquipos.listarEquipos();

		// lista de tipos únicos (antes de filtrar)
		List<String> listaTipos = contenidoBD.stream().map(EquiposResponseDTO::getTipoEquipo)
				.filter(t -> t != null && !t.trim().isEmpty()).map(String::trim).distinct()
				.sorted(String.CASE_INSENSITIVE_ORDER).toList();

		// filtrar si viene tipo
		if (tipo != null && !tipo.trim().isEmpty()) {
			String t = tipo.trim();
			contenidoBD = contenidoBD.stream()
					.filter(e -> e.getTipoEquipo() != null && e.getTipoEquipo().trim().equalsIgnoreCase(t)).toList();
		}

		contenidoBD = contenidoBD.stream().sorted(Comparator.comparing(EquiposResponseDTO::getIdEquipo)).toList();

		model.addAttribute("listarequipos", contenidoBD);
		model.addAttribute("listaTipos", listaTipos);
		model.addAttribute("tipoSeleccionado", tipo);

		return "Equipos/reporteEquipos";
	}

	@GetMapping("/reporte-equipo/excel")
	public ResponseEntity<byte[]> descargarExcelEquipos(@RequestParam(required = false) String tipo) {

		List<EquiposResponseDTO> data = servicioEquipos.listarEquipos();

		// Mismo filtro que en la vista
		if (tipo != null && !tipo.trim().isEmpty()) {
			String t = tipo.trim().toLowerCase();
			data = data.stream()
					.filter(e -> e.getTipoEquipo() != null && e.getTipoEquipo().trim().toLowerCase().equals(t))
					.toList();
		}

		byte[] excelBytes = equiposExcelService.generarReporteExcel(data, tipo);

		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String suf = (tipo != null && !tipo.isBlank()) ? "_" + tipo.trim().toLowerCase() : "";
		String filename = "reporte_equipos" + suf + "_" + timestamp + ".xlsx";

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType
						.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(excelBytes);
	}

}
