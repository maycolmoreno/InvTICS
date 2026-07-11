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

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CargosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.DepartamentosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CargosResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICargosServicio;
import com.uisrael.consumogestionactivosapi.service.IDepartamentosServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cargos")
public class CargosControlador {

	private final ICargosServicio servicioCargo;
	private final IDepartamentosServicio servicioDepartamento;

	@GetMapping
	public String listarCargos(Model model) {
		List<CargosResponseDTO> contenidoBD = servicioCargo.listarCargos();
		contenidoBD.sort(Comparator.comparing(CargosResponseDTO::getIdCargo));
		model.addAttribute("listacargo", contenidoBD);
		model.addAttribute("listadepartamento",
				servicioDepartamento.listarDepartamentos().stream().filter(d -> d.isEstado()).toList());
		return "cargos/listarCargos";
	}

	/** El alta/edicion ahora se hace desde un drawer en el listado. */
	@GetMapping("/nuevo-cargo")
	public String nuevoCargo() {
		return "redirect:/cargos";
	}

	/** El alta/edicion ahora se hace desde un drawer en el listado. */
	@GetMapping("/editar-cargo/{id}")
	public String modificarCargo() {
		return "redirect:/cargos";
	}

	@PostMapping
	public String guardarCargo(@ModelAttribute CargosRequestDTO cargo, RedirectAttributes redirectAttributes) {

		if (cargo.getFkDepartamento() == null) {
			cargo.setFkDepartamento(new DepartamentosRequestDTO());
		}

		if (cargo.getNombre() == null || cargo.getNombre().trim().isEmpty()) {
			return error(redirectAttributes, "El nombre es obligatorio");
		}

		boolean nombreRepetido = cargo.getIdCargo() > 0
				? servicioCargo.nombreExisteParaOtro(cargo.getNombre().trim(), cargo.getIdCargo())
				: servicioCargo.nombreExiste(cargo.getNombre().trim());
		if (nombreRepetido) {
			return error(redirectAttributes, "Ya existe un cargo con ese nombre");
		}

		if (cargo.getFkDepartamento().getIdDepartamento() <= 0) {
			return error(redirectAttributes, "Debe seleccionar un departamento");
		}

		if (cargo.getIdCargo() <= 0) {
			cargo.setEstado(true);
		}

		if (cargo.getIdCargo() > 0) {
			servicioCargo.actualizarCargo(cargo.getIdCargo(), cargo);
			redirectAttributes.addFlashAttribute("success", "Cargo actualizado correctamente.");
		} else {
			servicioCargo.crearCargo(cargo);
			redirectAttributes.addFlashAttribute("success", "Cargo creado correctamente.");
		}

		return "redirect:/cargos";
	}

	private String error(RedirectAttributes redirectAttributes, String mensaje) {
		redirectAttributes.addFlashAttribute("error", mensaje);
		return "redirect:/cargos";
	}

	@PostMapping("/eliminar-cargo")
	public String eliminarLogico(@RequestParam Integer idCargo) {
		servicioCargo.actualizarEstado(idCargo, false);
		return "redirect:/cargos";
	}

}
