package com.uisrael.consumogestionactivosapi.controlador;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.DepartamentosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.UbicacionesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionesResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IDepartamentosServicio;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ubicaciones") // url
public class UbicacionesControlador {

	private final IUbicacionesServicio servicioUbicacion;
	private final IDepartamentosServicio servicioDepartamento;
	private final ICustodiosServicio servicioCustodio;

	@GetMapping
	public String listarUbicaciones(Model model) {
		List<UbicacionesResponseDTO> contenidoBD = servicioUbicacion.listarUbicaciones();
		contenidoBD.sort(Comparator.comparing(UbicacionesResponseDTO::getIdUbicacion));
		model.addAttribute("listaubicacion", contenidoBD);
		model.addAttribute("listadepartamento",
				servicioDepartamento.listarDepartamentos().stream().filter(d -> d.isEstado()).toList());
		model.addAttribute("listacustodio",
				servicioCustodio.listarCustodios().stream().filter(c -> c.isEstado()).toList());
		return "ubicaciones/listarUbicaciones";
	}

	/** El alta/edicion ahora se hace desde un drawer en el listado. */
	@GetMapping("/nueva-ubicacion")
	public String nuevaUbicacion() {
		return "redirect:/ubicaciones";
	}

	/** El alta/edicion ahora se hace desde un drawer en el listado. */
	@GetMapping("/editar-ubicacion/{id}")
	public String modificarUbicacion() {
		return "redirect:/ubicaciones";
	}

	@PostMapping
	public String guardarUbicacion(@ModelAttribute UbicacionesRequestDTO ubicacion,
			RedirectAttributes redirectAttributes) {

		if (ubicacion.getFkDepartamento() == null) {
			ubicacion.setFkDepartamento(new DepartamentosRequestDTO());
		}

		if (ubicacion.getNombre() == null || ubicacion.getNombre().trim().isEmpty()) {
			return error(redirectAttributes, "El nombre es obligatorio");
		}

		boolean nombreRepetido = ubicacion.getIdUbicacion() > 0
				? servicioUbicacion.nombreExisteParaOtro(ubicacion.getNombre().trim(), ubicacion.getIdUbicacion())
				: servicioUbicacion.nombreExiste(ubicacion.getNombre().trim());
		if (nombreRepetido) {
			return error(redirectAttributes, "Ya existe una ubicación con ese nombre");
		}

		if (ubicacion.getAgencia() == null || ubicacion.getAgencia().trim().isEmpty()) {
			return error(redirectAttributes, "La agencia es obligatoria");
		}

		if (ubicacion.getFkDepartamento().getIdDepartamento() <= 0) {
			return error(redirectAttributes, "Debe seleccionar un departamento");
		}

		// El encargado es opcional: 0 (opcion "Sin encargado" del select) equivale a ninguno.
		if (ubicacion.getIdCustodioEncargado() != null && ubicacion.getIdCustodioEncargado() <= 0) {
			ubicacion.setIdCustodioEncargado(null);
		}

		if (ubicacion.getIdUbicacion() > 0) {
			servicioUbicacion.actualizarUbicacion(ubicacion.getIdUbicacion(), ubicacion);
			redirectAttributes.addFlashAttribute("success", "Ubicación actualizada correctamente.");
		} else {
			ubicacion.setEstado(true);
			servicioUbicacion.crearUbicacion(ubicacion);
			redirectAttributes.addFlashAttribute("success", "Ubicación creada correctamente.");
		}

		return "redirect:/ubicaciones";
	}

	private String error(RedirectAttributes redirectAttributes, String mensaje) {
		redirectAttributes.addFlashAttribute("error", mensaje);
		return "redirect:/ubicaciones";
	}

	@PostMapping("/eliminar-logico")
	public String eliminarLogico(@RequestParam Integer idUbicacion) {
		servicioUbicacion.actualizarEstado(idUbicacion, false);
		return "redirect:/ubicaciones";
	}

}
