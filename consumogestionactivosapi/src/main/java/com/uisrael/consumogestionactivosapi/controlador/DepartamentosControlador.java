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
import com.uisrael.consumogestionactivosapi.modelo.dto.request.UbicacionesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.DepartamentosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionesResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IDepartamentosServicio;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/departamentos") // url
public class DepartamentosControlador {

	private final IDepartamentosServicio servicioDepartamento;
	private final IUbicacionesServicio servicioUbicacion;

	@GetMapping
	public String listarDepartamentos(Model model) {
		List<DepartamentosResponseDTO> contenidoBD = servicioDepartamento.listarDepartamentos();
		contenidoBD.sort(Comparator.comparing(DepartamentosResponseDTO::getIdDepartamento));
		model.addAttribute("listadepartamento", contenidoBD);
		return "departamentos/listarDepartamentos"; // ubicacion fisica page
	}

	@GetMapping("/nuevo-departamento")
	public String nuevoDepartamento(Model model) {
		DepartamentosRequestDTO departamento = new DepartamentosRequestDTO();
		departamento.setEstado(true);

		// ✅ IMPORTANTE: crear el objeto para que th:field fkUbicacion.idUbicacion
		// funcione
		departamento.setFkUbicacion(new UbicacionesRequestDTO());
		departamento.getFkUbicacion().setIdUbicacion(0);

		var ubicacionesActivas = servicioUbicacion.listarUbicaciones().stream().filter(u -> u.isEstado()).toList();

		model.addAttribute("listaubicacion", ubicacionesActivas);
		model.addAttribute("departamento", departamento);
		return "departamentos/nuevoDepartamento"; // ubicacion fisica page
	}

	@GetMapping("/editar-departamento/{id}")
	public String modificarDepartamento(@PathVariable Integer id, Model model) {
		DepartamentosResponseDTO departamento = servicioDepartamento.obtenerPorId(id);
		if (departamento.getFkUbicacion() == null) {
			UbicacionesResponseDTO ubicacion = new UbicacionesResponseDTO();
			ubicacion.setIdUbicacion(0);
			departamento.setFkUbicacion(ubicacion);
		}
		Integer idUbicacionDepartamento = departamento.getFkUbicacion().getIdUbicacion();

		model.addAttribute("listaubicacion",
				servicioUbicacion.listarUbicaciones().stream().filter(
						ubicacion -> ubicacion.isEstado() || ubicacion.getIdUbicacion() == idUbicacionDepartamento)
						.toList());

		model.addAttribute("departamento", departamento);

		return "departamentos/editarDepartamento"; // ubicacion fisica page
	}

	@PostMapping
	public String guardarDepartametno(@ModelAttribute DepartamentosRequestDTO departamento, Model model) {

		// 🔒 Asegura objeto anidado
		if (departamento.getFkUbicacion() == null) {
			departamento.setFkUbicacion(new UbicacionesRequestDTO());
		}

		boolean hayErrores = false;

		// 1️⃣ Nombre obligatorio
		if (departamento.getNombre() == null || departamento.getNombre().trim().isEmpty()) {
			model.addAttribute("errorNombre", "El nombre es obligatorio");
			hayErrores = true;
		} else {
			// 2️⃣ Nombre no repetido
			boolean nombreRepetido;

			if (departamento.getIdDepartamento() > 0) {
				// edición
				nombreRepetido = servicioDepartamento.nombreExisteParaOtro(departamento.getNombre().trim(),
						departamento.getIdDepartamento());
			} else {
				// creación
				nombreRepetido = servicioDepartamento.nombreExiste(departamento.getNombre().trim());
			}

			if (nombreRepetido) {
				model.addAttribute("errorNombre", "Ya existe un departamento con ese nombre");
				hayErrores = true;
			}
		}

		// 3️⃣ Ubicación obligatoria
		if (departamento.getFkUbicacion().getIdUbicacion() <= 0) {
			model.addAttribute("errorSeleccionUbicacion", "Debe seleccionar una ubicación");
			hayErrores = true;
		}

		// 🔴 Si hay errores, vuelve al formulario
		if (hayErrores) {
			model.addAttribute("listaubicacion",
					servicioUbicacion.listarUbicaciones().stream().filter(u -> u.isEstado()).toList());
			model.addAttribute("departamento", departamento);
			return ubicacionesFormulario(departamento);
		}

		// 4️⃣ Guardar
		if (departamento.getIdDepartamento() > 0) {
			servicioDepartamento.actualizarDepartamento(departamento.getIdDepartamento(), departamento);
		} else {
			departamento.setEstado(true);
			servicioDepartamento.crearDepartamento(departamento);
		}

		return "redirect:/departamentos";
	}

	private String ubicacionesFormulario(DepartamentosRequestDTO departamento) {
		return (departamento.getIdDepartamento() > 0) ? "departamentos/editarDepartamento"
				: "departamentos/nuevoDepartamento";
	}

	@PostMapping("/eliminar-departamento")
	public String eliminarLogico(@org.springframework.web.bind.annotation.RequestParam Integer idDepartamento) {
		servicioDepartamento.actualizarEstado(idDepartamento, false);
		return "redirect:/departamentos";
	}
}
