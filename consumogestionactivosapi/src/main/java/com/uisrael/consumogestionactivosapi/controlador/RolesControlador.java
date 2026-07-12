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

	/** El alta/edicion ahora se hace desde un drawer en el listado. */
	@GetMapping("/nuevo-rol")
	public String nuevoRol() {
		return "redirect:/roles";
	}

	/** El alta/edicion ahora se hace desde un drawer en el listado. */
	@GetMapping("/editar-rol/{id}")
	public String editarRol() {
		return "redirect:/roles";
	}

	@PostMapping
	public String guardarRol(@ModelAttribute RolesRequestDTO nuevorol, RedirectAttributes redirectAttributes) {
		if (nuevorol.getNombre() == null || nuevorol.getNombre().trim().isEmpty()) {
			return error(redirectAttributes, "El nombre es obligatorio");
		}

		try {
			if (nuevorol.getIdRol() > 0) {
				servicioRoles.actualizarRol(nuevorol.getIdRol(), nuevorol);
				redirectAttributes.addFlashAttribute("success", "Rol actualizado correctamente.");
			} else {
				nuevorol.setEstado(true);
				servicioRoles.nuevoRol(nuevorol);
				redirectAttributes.addFlashAttribute("success", "Rol creado correctamente.");
			}
		} catch (BackendException e) {
			return error(redirectAttributes, e.getMessage());
		}

		return "redirect:/roles";
	}

	private String error(RedirectAttributes redirectAttributes, String mensaje) {
		redirectAttributes.addFlashAttribute("error", mensaje);
		return "redirect:/roles";
	}

	@PostMapping("/eliminar/{id}")
	public String eliminarRol(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		try {
			servicioRoles.eliminarRol(id);
		} catch (BackendException e) {
			redirectAttributes.addFlashAttribute("errorEliminar", e.getMessage());
		}
		return "redirect:/roles";
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
