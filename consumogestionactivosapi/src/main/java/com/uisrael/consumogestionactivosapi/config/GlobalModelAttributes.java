package com.uisrael.consumogestionactivosapi.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.uisrael.consumogestionactivosapi.security.SesionUsuario;
import com.uisrael.consumogestionactivosapi.service.INotificacionServicio;

@ControllerAdvice
public class GlobalModelAttributes {

	private final SesionUsuario sesionUsuario;
	private final INotificacionServicio notificacionServicio;

	public GlobalModelAttributes(SesionUsuario sesionUsuario, INotificacionServicio notificacionServicio) {
		this.sesionUsuario = sesionUsuario;
		this.notificacionServicio = notificacionServicio;
	}

	@ModelAttribute("sesionUsuario")
	public SesionUsuario obtenerSesionUsuario() {
		return sesionUsuario;
	}

	@ModelAttribute("notificacionesPendientes")
	public Long notificacionesPendientes() {
		if (!sesionUsuario.isAutenticado()) {
			return 0L;
		}
		try {
			return notificacionServicio.contarNoLeidas();
		} catch (Exception ex) {
			return 0L;
		}
	}
}
