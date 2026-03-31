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

	@GetMapping("/nuevo-departamento")
	public String nuevoDepartamento(Model model) {
		DepartamentosRequestDTO departamento = new DepartamentosRequestDTO();
		departamento.setEstado(true);
		model.addAttribute("departamento", departamento);
		return "departamentos/nuevoDepartamento";
	}

	@GetMapping("/editar-departamento/{id}")
	public String modificarDepartamento(@PathVariable Integer id, Model model) {
		DepartamentosResponseDTO departamento = servicioDepartamento.obtenerPorId(id);
		model.addAttribute("departamento", departamento);
		return "departamentos/editarDepartamento";
	}

	@PostMapping
	public String guardarDepartamento(@ModelAttribute DepartamentosRequestDTO departamento, Model model) {

		boolean hayErrores = false;

		if (departamento.getNombre() == null || departamento.getNombre().trim().isEmpty()) {
			model.addAttribute("errorNombre", "El nombre es obligatorio");
			hayErrores = true;
		} else {
			boolean nombreRepetido;
			if (departamento.getIdDepartamento() > 0) {
				nombreRepetido = servicioDepartamento.nombreExisteParaOtro(departamento.getNombre().trim(),
						departamento.getIdDepartamento());
			} else {
				nombreRepetido = servicioDepartamento.nombreExiste(departamento.getNombre().trim());
			}
			if (nombreRepetido) {
				model.addAttribute("errorNombre", "Ya existe un departamento con ese nombre");
				hayErrores = true;
			}
		}

		if (hayErrores) {
			model.addAttribute("departamento", departamento);
			return formularioDepartamento(departamento);
		}

		if (departamento.getIdDepartamento() > 0) {
			servicioDepartamento.actualizarDepartamento(departamento.getIdDepartamento(), departamento);
		} else {
			departamento.setEstado(true);
			servicioDepartamento.crearDepartamento(departamento);
		}

		return "redirect:/departamentos";
	}

	private String formularioDepartamento(DepartamentosRequestDTO departamento) {
		return (departamento.getIdDepartamento() > 0) ? "departamentos/editarDepartamento"
				: "departamentos/nuevoDepartamento";
	}

	@PostMapping("/eliminar-departamento")
	public String eliminarLogico(@org.springframework.web.bind.annotation.RequestParam Integer idDepartamento) {
		servicioDepartamento.actualizarEstado(idDepartamento, false);
		return "redirect:/departamentos";
	}
}
