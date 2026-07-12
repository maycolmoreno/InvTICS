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
import com.uisrael.consumogestionactivosapi.modelo.dto.response.DepartamentosResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IDepartamentosServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/departamentos")
public class DepartamentosControlador {

	private final IDepartamentosServicio servicioDepartamento;

	@GetMapping
	public String listarDepartamentos(Model model) {
		List<DepartamentosResponseDTO> contenidoBD = servicioDepartamento.listarDepartamentos();
		contenidoBD.sort(Comparator.comparing(DepartamentosResponseDTO::getIdDepartamento));
		model.addAttribute("listadepartamento", contenidoBD);
		return "departamentos/listarDepartamentos";
	}

	/** El alta/edicion ahora se hace desde un drawer en el listado. */
	@GetMapping("/nuevo-departamento")
	public String nuevoDepartamento() {
		return "redirect:/departamentos";
	}

	/** El alta/edicion ahora se hace desde un drawer en el listado. */
	@GetMapping("/editar-departamento/{id}")
	public String modificarDepartamento() {
		return "redirect:/departamentos";
	}

	@PostMapping
	public String guardarDepartamento(@ModelAttribute DepartamentosRequestDTO departamento,
			RedirectAttributes redirectAttributes) {

		if (departamento.getNombre() == null || departamento.getNombre().trim().isEmpty()) {
			return error(redirectAttributes, "El nombre es obligatorio");
		}

		boolean nombreRepetido = departamento.getIdDepartamento() > 0
				? servicioDepartamento.nombreExisteParaOtro(departamento.getNombre().trim(),
						departamento.getIdDepartamento())
				: servicioDepartamento.nombreExiste(departamento.getNombre().trim());
		if (nombreRepetido) {
			return error(redirectAttributes, "Ya existe un departamento con ese nombre");
		}

		if (departamento.getIdDepartamento() > 0) {
			servicioDepartamento.actualizarDepartamento(departamento.getIdDepartamento(), departamento);
			redirectAttributes.addFlashAttribute("success", "Departamento actualizado correctamente.");
		} else {
			departamento.setEstado(true);
			servicioDepartamento.crearDepartamento(departamento);
			redirectAttributes.addFlashAttribute("success", "Departamento creado correctamente.");
		}

		return "redirect:/departamentos";
	}

	private String error(RedirectAttributes redirectAttributes, String mensaje) {
		redirectAttributes.addFlashAttribute("error", mensaje);
		return "redirect:/departamentos";
	}

	@PostMapping("/eliminar-departamento")
	public String eliminarLogico(@RequestParam Integer idDepartamento) {
		servicioDepartamento.actualizarEstado(idDepartamento, false);
		return "redirect:/departamentos";
	}
}
