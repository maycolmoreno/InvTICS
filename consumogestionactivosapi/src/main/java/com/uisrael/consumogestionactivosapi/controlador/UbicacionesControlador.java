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

import com.uisrael.consumogestionactivosapi.modelo.dto.request.UbicacionesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionesResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ubicaciones") // url
public class UbicacionesControlador {

	private final IUbicacionesServicio servicioUbicacion;

	@GetMapping
	public String listarUbicaciones(Model model) {
		List<UbicacionesResponseDTO> contenidoBD = servicioUbicacion.listarUbicaciones();
		contenidoBD.sort(Comparator.comparing(UbicacionesResponseDTO::getIdUbicacion));
		model.addAttribute("listaubicacion", contenidoBD);
		return "ubicaciones/listarUbicaciones"; // ubicacion fisica page
	}

	@GetMapping("/nueva-ubicacion")
	public String nuevaUbicacion(Model model) {
		UbicacionesRequestDTO ubicacion = new UbicacionesRequestDTO();
		ubicacion.setEstado(true); // ✅ por defecto ACTIVO
		model.addAttribute("ubicacion", ubicacion);
		return "ubicaciones/nuevaUbicacion"; // ubicacion fisica page
	}

	@GetMapping("/editar-ubicacion/{id}")
	public String modificarUbicacion(@PathVariable Integer id, Model model) {

		UbicacionesResponseDTO ubicacion = servicioUbicacion.obtenerPorId(id);

		model.addAttribute("ubicacion", ubicacion);

		return "ubicaciones/editarUbicacion";
	}

	@PostMapping
	public String guardarUbicacion(@ModelAttribute UbicacionesRequestDTO ubicacion, Model model) {

		boolean hayErrores = false;

		if (ubicacion.getNombre() == null || ubicacion.getNombre().trim().isEmpty()) {
			model.addAttribute("errorNombre", "El nombre es obligatorio");
			hayErrores = true;
		} else {
			// 2️⃣ Nombre no repetido
			boolean nombreRepetido;

			if (ubicacion.getIdUbicacion() > 0) {
				// edición
				nombreRepetido = servicioUbicacion.nombreExisteParaOtro(ubicacion.getNombre().trim(),
						ubicacion.getIdUbicacion());
			} else {
				// creación
				nombreRepetido = servicioUbicacion.nombreExiste(ubicacion.getNombre().trim());
			}

			if (nombreRepetido) {
				model.addAttribute("errorNombre", "Ya existe una ubicación con ese nombre");
				hayErrores = true;
			}
		}

		if (ubicacion.getAgencia() == null || ubicacion.getAgencia().trim().isEmpty()) {
			model.addAttribute("errorAgencia", "La agencia es obligatoria");
			hayErrores = true;
		}

		// 🔴 Si hay errores, regreso al formulario
		if (hayErrores) {
			model.addAttribute("ubicacion", ubicacion);
			return ubicacionesFormulario(ubicacion);
		}

		// Guardar o actualizar

		if (ubicacion.getIdUbicacion() > 0) {
			servicioUbicacion.actualizarUbicacion(ubicacion.getIdUbicacion(), ubicacion);
		} else {
			ubicacion.setEstado(true);
			servicioUbicacion.crearUbicacion(ubicacion);
		}

		return "redirect:/ubicaciones";
	}

	private String ubicacionesFormulario(UbicacionesRequestDTO ubicacion) {
		return (ubicacion.getIdUbicacion() > 0) ? "ubicaciones/editarUbicacion" : "ubicaciones/nuevaUbicacion";
	}

	@PostMapping("/eliminar-logico")
	public String eliminarLogico(@org.springframework.web.bind.annotation.RequestParam Integer idUbicacion) {
		servicioUbicacion.actualizarEstado(idUbicacion, false);
		return "redirect:/ubicaciones";
	}

}
