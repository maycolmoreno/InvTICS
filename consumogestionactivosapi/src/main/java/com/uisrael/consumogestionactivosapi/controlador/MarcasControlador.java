package com.uisrael.consumogestionactivosapi.controlador;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.exception.BackendException;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.MarcasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MarcasResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/marcas")
public class MarcasControlador {

	private final IMarcasServicio servicioMarcas;

	@GetMapping
	public String listarMarcas(Model model) {
		List<MarcasResponseDTO> contenidoBD = servicioMarcas.listarMarca();
		model.addAttribute("listarmarcas", contenidoBD);
		return "marcas/listarMarcas";
	}

	/** El alta/edicion ahora se hace desde un drawer en el listado. */
	@GetMapping("/nuevaMarcas")
	public String nuevaMarcas() {
		return "redirect:/marcas";
	}

	/** El alta/edicion ahora se hace desde un drawer en el listado. */
	@GetMapping("/editarMarcas/{id}")
	public String editarMarca() {
		return "redirect:/marcas";
	}

	@PostMapping
	public String guardarMarcas(@ModelAttribute MarcasRequestDTO marca, RedirectAttributes redirectAttributes) {
		if (marca.getNombre() == null || marca.getNombre().trim().isEmpty()) {
			return error(redirectAttributes, "El nombre es obligatorio");
		}

		try {
			if (marca.getIdMarca() > 0) {
				servicioMarcas.actualizarMarca(marca.getIdMarca(), marca);
				redirectAttributes.addFlashAttribute("success", "Marca actualizada correctamente.");
			} else {
				marca.setEstado(true);
				servicioMarcas.nuevaMarca(marca);
				redirectAttributes.addFlashAttribute("success", "Marca creada correctamente.");
			}
		} catch (IllegalArgumentException e) {
			return error(redirectAttributes, e.getMessage());
		}

		return "redirect:/marcas";
	}

	private String error(RedirectAttributes redirectAttributes, String mensaje) {
		redirectAttributes.addFlashAttribute("error", mensaje);
		return "redirect:/marcas";
	}

	@PostMapping("/eliminar/{id}")
	public String eliminarMarca(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		try {
			servicioMarcas.eliminarMarca(id);
		} catch (BackendException e) {
			redirectAttributes.addFlashAttribute("errorEliminar", e.getMessage());
		}
		return "redirect:/marcas";
	}

}
