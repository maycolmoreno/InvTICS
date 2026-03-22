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
		return "cargos/listarCargos"; // ubicacion fisica page
	}

	@GetMapping("/nuevo-cargo")
	public String nuevoCargo(Model model) {
		CargosRequestDTO cargo = new CargosRequestDTO();
		cargo.setEstado(true);

		cargo.setFkDepartamento(new DepartamentosRequestDTO());
		cargo.getFkDepartamento().setIdDepartamento(0);

		var departamentosActivos = servicioDepartamento.listarDepartamentos().stream()
				.filter(d -> d.isEstado()).toList();

		model.addAttribute("listadepartamento", departamentosActivos);
		model.addAttribute("cargo", cargo);
		return "cargos/nuevoCargo";
	}

	@GetMapping("/editar-cargo/{id}")
	public String modificarCargo(@PathVariable Integer id, Model model) {
		CargosResponseDTO cargo = servicioCargo.obtenerPorId(id);

		Integer idDepartamentoCargo = (cargo.getFkDepartamento() != null)
				? cargo.getFkDepartamento().getIdDepartamento() : 0;

		model.addAttribute("listadepartamento",
				servicioDepartamento.listarDepartamentos().stream().filter(
						dep -> dep.isEstado() || dep.getIdDepartamento() == idDepartamentoCargo)
						.toList());

		model.addAttribute("cargo", cargo);

		return "cargos/editarCargo";
	}

	@PostMapping
	public String guardarCargo(@ModelAttribute CargosRequestDTO cargo, Model model) {

		// Asegurar objeto anidado
		if (cargo.getFkDepartamento() == null) {
			cargo.setFkDepartamento(new DepartamentosRequestDTO());
		}

	    boolean hayErrores = false;

	    // 1️⃣ Nombre obligatorio
	    if (cargo.getNombre() == null || cargo.getNombre().trim().isEmpty()) {
	        model.addAttribute("errorNombre", "El nombre es obligatorio");
	        hayErrores = true;
	    } else {
	        // 2️⃣ Nombre no repetido
	        boolean nombreRepetido;

	        if (cargo.getIdCargo() > 0) {
	            // edición
	            nombreRepetido = servicioCargo.nombreExisteParaOtro(
	            		cargo.getNombre().trim(), cargo.getIdCargo());
	        } else {
	            // creación
	            nombreRepetido = servicioCargo.nombreExiste(cargo.getNombre().trim());
	        }

	        if (nombreRepetido) {
	            model.addAttribute("errorNombre", "Ya existe un cargo con ese nombre");
	            hayErrores = true;
	        }
	    }

	    // 3️⃣ Departamento obligatorio
	    if (cargo.getFkDepartamento().getIdDepartamento() <= 0) {
	        model.addAttribute("errorSeleccionDepartamento", "Debe seleccionar un departamento");
	        hayErrores = true;
	    }

	    // 🔴 Si hay errores, vuelve al formulario
	    if (hayErrores) {
	        model.addAttribute("listadepartamento",
	        		servicioDepartamento.listarDepartamentos().stream().filter(d -> d.isEstado()).toList());
	        model.addAttribute("cargo", cargo);
	        return cargosFormulario(cargo);
	    }

	    if (cargo.getIdCargo() <= 0) {
	        cargo.setEstado(true);
	    }

	    // 4️⃣ Guardar
	    if (cargo.getIdCargo() > 0) {
	        servicioCargo.actualizarCargo(cargo.getIdCargo(), cargo);
	    } else {
	        servicioCargo.crearCargo(cargo);
	    }

	    return "redirect:/cargos";
	}

	private String cargosFormulario(CargosRequestDTO cargo) {
		return (cargo.getIdCargo() > 0) ? "cargos/editarCargo"
				: "cargos/nuevoCargo";
	}

	@PostMapping("/eliminar-cargo")
	public String eliminarLogico(@org.springframework.web.bind.annotation.RequestParam Integer idCargo) {
		servicioCargo.actualizarEstado(idCargo, false);
		return "redirect:/cargos";
	}

}
