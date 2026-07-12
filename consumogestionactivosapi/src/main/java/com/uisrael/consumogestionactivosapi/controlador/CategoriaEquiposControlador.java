package com.uisrael.consumogestionactivosapi.controlador;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CategoriaEquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CategoriaEquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.exception.BackendException;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categorias-equipo")
public class CategoriaEquiposControlador {

	private final ICategoriaEquiposServicio servicioCategoriaEquipos;

	@GetMapping
	public String listarCategoriaEquipos(Model model) {
		List<CategoriaEquiposResponseDTO> contenidoBD = servicioCategoriaEquipos.listarCategoriaEquipo();
		contenidoBD.sort(Comparator.comparing(CategoriaEquiposResponseDTO::getIdCategoria));
		model.addAttribute("listarcategorias", contenidoBD);
		return "categorias_equipo/listarCategorias";
	}

	/** El alta/edicion ahora se hace desde un drawer en el listado. */
	@GetMapping("/nueva-categoria")
	public String nuevaCategoria() {
		return "redirect:/categorias-equipo";
	}

	/** El alta/edicion ahora se hace desde un drawer en el listado. */
	@GetMapping("/editar-categoria/{id}")
	public String editarCategoria() {
		return "redirect:/categorias-equipo";
	}

	@PostMapping
	public String guardarCategoria(@ModelAttribute CategoriaEquiposRequestDTO nuevacategoria,
			RedirectAttributes redirectAttributes) {
		if (nuevacategoria.getNombre() == null || nuevacategoria.getNombre().trim().isEmpty()) {
			return error(redirectAttributes, "El nombre es obligatorio");
		}

		try {
			if (nuevacategoria.getIdCategoria() > 0) {
				servicioCategoriaEquipos.actualizarCategoriaEquipo(nuevacategoria.getIdCategoria(), nuevacategoria);
				redirectAttributes.addFlashAttribute("success", "Categoria actualizada correctamente.");
			} else {
				nuevacategoria.setEstado(true);
				servicioCategoriaEquipos.nuevoCategoriaEquipo(nuevacategoria);
				redirectAttributes.addFlashAttribute("success", "Categoria creada correctamente.");
			}
		} catch (BackendException e) {
			return error(redirectAttributes, e.getMessage());
		}

		return "redirect:/categorias-equipo";
	}

	private String error(RedirectAttributes redirectAttributes, String mensaje) {
		redirectAttributes.addFlashAttribute("error", mensaje);
		return "redirect:/categorias-equipo";
	}

	@PostMapping("/eliminar/{id}")
	public String eliminarCategoria(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		try {
			servicioCategoriaEquipos.eliminarCategoriaEquipo(id);
		} catch (BackendException e) {
			redirectAttributes.addFlashAttribute("errorEliminar", e.getMessage());
		}
		return "redirect:/categorias-equipo";
	}
}
