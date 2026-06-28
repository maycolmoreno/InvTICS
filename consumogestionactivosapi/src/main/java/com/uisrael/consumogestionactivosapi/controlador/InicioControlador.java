package com.uisrael.consumogestionactivosapi.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioControlador {

	@GetMapping("/inicio")
	public String mostrarInicio() {
		return "redirect:/inventario/dashboard";
	}
}
