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

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CategoriaEquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CategoriaEquiposResponseDTO;
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

	@GetMapping("/nueva-categoria")
	public String nuevaCategoria(Model model) {
		model.addAttribute("nuevacategoria", new CategoriaEquiposRequestDTO());
		return "categorias_equipo/nuevaCategoria";
	}

	@PostMapping
	public String guardarCategoria(@ModelAttribute CategoriaEquiposRequestDTO nuevacategoria, Model model) {
		try {
			if (nuevacategoria.getIdCategoria() > 0) {
				servicioCategoriaEquipos.actualizarCategoriaEquipo(nuevacategoria.getIdCategoria(), nuevacategoria);
			} else {
				nuevacategoria.setEstado(true);
				servicioCategoriaEquipos.nuevoCategoriaEquipo(nuevacategoria);
			}
			return "redirect:/categorias-equipo";
		} catch (RuntimeException e) {
			model.addAttribute("errorNombre", e.getMessage());
			model.addAttribute("nuevacategoria", nuevacategoria);
			return nuevacategoria.getIdCategoria() > 0 ? "categorias_equipo/editarCategoria"
					: "categorias_equipo/nuevaCategoria";
		}
	}

	@GetMapping("/editar-categoria/{id}")
	public String editarCategoria(@PathVariable Integer id, Model model) {
		CategoriaEquiposResponseDTO categoria = servicioCategoriaEquipos.obtenerCategoriaEquipo(id);
		model.addAttribute("nuevacategoria", categoria);
		return "categorias_equipo/editarCategoria";
	}

	@PostMapping("/eliminar/{id}")
	public String eliminarCategoria(@PathVariable Integer id, Model model) {
		try {
			servicioCategoriaEquipos.eliminarCategoriaEquipo(id);
			return "redirect:/categorias-equipo";
		} catch (RuntimeException e) {
			List<CategoriaEquiposResponseDTO> contenidoBD = servicioCategoriaEquipos.listarCategoriaEquipo();
			model.addAttribute("listarcategorias", contenidoBD);
			model.addAttribute("errorEliminar", e.getMessage());
			return "categorias_equipo/listarCategorias";
		}
	}
}
