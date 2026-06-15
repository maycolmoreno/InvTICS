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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.RolesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.ModuloResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.RolesResponseDTO;
import com.uisrael.consumogestionactivosapi.exception.BackendException;
import com.uisrael.consumogestionactivosapi.service.IModulosServicio;
import com.uisrael.consumogestionactivosapi.service.IRolesServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RolesControlador {

	private final IRolesServicio servicioRoles;
	private final IModulosServicio servicioModulos;

	@GetMapping
	public String listarRoles(Model model) {
		List<RolesResponseDTO> contenidoBD = servicioRoles.listarRol();
		contenidoBD.sort(Comparator.comparing(RolesResponseDTO::getIdRol));
		model.addAttribute("listarroles", contenidoBD);
		return "roles/listarRoles";
	}

	@GetMapping("/nuevo-rol")
	public String nuevoRol(Model model) {
		model.addAttribute("nuevorol", new RolesRequestDTO());
		return "roles/nuevoRol";
	}

	@PostMapping
	public String guardarRol(@ModelAttribute RolesRequestDTO nuevorol, Model model) {
		try {
			if (nuevorol.getIdRol() > 0) {
				servicioRoles.actualizarRol(nuevorol.getIdRol(), nuevorol);
			} else {
				nuevorol.setEstado(true);
				servicioRoles.nuevoRol(nuevorol);
			}
			return "redirect:/roles";
		} catch (BackendException e) {
			model.addAttribute("errorNombre", e.getMessage());
			model.addAttribute("nuevorol", nuevorol);
			return nuevorol.getIdRol() > 0 ? "roles/editarRol" : "roles/nuevoRol";
		}
	}

	@GetMapping("/editar-rol/{id}")
	public String editarRol(@PathVariable Integer id, Model model) {
		RolesResponseDTO rol = servicioRoles.obtenerRol(id);
		model.addAttribute("nuevorol", rol);
		return "roles/editarRol";
	}

	@PostMapping("/eliminar/{id}")
	public String eliminarRol(@PathVariable Integer id, Model model) {
		try {
			servicioRoles.eliminarRol(id);
			return "redirect:/roles";
		} catch (BackendException e) {
			List<RolesResponseDTO> contenidoBD = servicioRoles.listarRol();
			model.addAttribute("listarroles", contenidoBD);
			model.addAttribute("errorEliminar", e.getMessage());
			return "roles/listarRoles";
		}
	}

	@GetMapping("/permisos/{id}")
	public String verPermisos(@PathVariable Integer id, Model model) {
		RolesResponseDTO rol = servicioRoles.obtenerRol(id);
		List<ModuloResponseDTO> modulos = servicioModulos.listarModulosPorRol(id);
		model.addAttribute("rol", rol);
		model.addAttribute("modulos", modulos);
		return "roles/permisos";
	}

	@PostMapping("/permisos/{id}")
	public String guardarPermisos(@PathVariable Integer id,
								  @RequestParam(value = "moduloIds", required = false) List<Integer> moduloIds,
								  RedirectAttributes redirectAttributes) {
		try {
			servicioModulos.actualizarModulosRol(id, moduloIds != null ? moduloIds : List.of());
			redirectAttributes.addFlashAttribute("exito", "Permisos actualizados correctamente.");
		} catch (BackendException e) {
			redirectAttributes.addFlashAttribute("error", "Error al actualizar permisos: " + e.getMessage());
		}
		return "redirect:/roles/permisos/" + id;
	}
}
