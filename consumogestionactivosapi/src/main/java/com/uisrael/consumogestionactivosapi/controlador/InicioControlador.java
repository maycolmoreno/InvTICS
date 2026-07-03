package com.uisrael.consumogestionactivosapi.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.uisrael.consumogestionactivosapi.security.SesionUsuario;

@Controller
public class InicioControlador {

	private final SesionUsuario sesionUsuario;

	public InicioControlador(SesionUsuario sesionUsuario) {
		this.sesionUsuario = sesionUsuario;
	}

	/**
	 * Aterrizaje según los módulos del rol: el dashboard operativo exige
	 * INVENTARIO; un TECNICO aterriza en sus órdenes de trabajo.
	 */
	@GetMapping("/inicio")
	public String mostrarInicio() {
		if (sesionUsuario.tieneModulo("INVENTARIO")) {
			return "redirect:/inventario/dashboard";
		}
		if (sesionUsuario.tieneModulo("MANTENIMIENTO")) {
			return "redirect:/mantenimiento";
		}
		if (sesionUsuario.tieneModulo("REPORTES")) {
			return "redirect:/reportes";
		}
		if (sesionUsuario.tieneModulo("NOTIFICACIONES")) {
			return "redirect:/notificaciones";
		}
		return "redirect:/logout";
	}
}
