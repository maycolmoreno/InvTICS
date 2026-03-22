package com.uisrael.consumogestionactivosapi.security;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Datos de autenticación del usuario almacenados en la sesión HTTP
 */
@Component
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SesionUsuario {

	private String correo;
	private String contrasena;
	private String nombreUsuario;
	private String rol;
	private boolean autenticado;

	public SesionUsuario() {
		this.autenticado = false;
	}

	public void iniciarSesion(String correo, String contrasena, String nombreUsuario, String rol) {
		this.correo = correo;
		this.contrasena = contrasena;
		this.nombreUsuario = nombreUsuario;
		this.rol = rol;
		this.autenticado = true;
	}

	public void cerrarSesion() {
		this.correo = null;
		this.contrasena = null;
		this.nombreUsuario = null;
		this.rol = null;
		this.autenticado = false;
	}

	public String getCorreo() {
		return correo;
	}

	public String getContrasena() {
		return contrasena;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public String getRol() {
		return rol;
	}

	public boolean isAutenticado() {
		return autenticado;
	}

	public boolean tieneRol(String rolEsperado) {
		return this.rol != null && this.rol.equals(rolEsperado);
	}

	public boolean tieneAlgunRol(String... roles) {
		if (this.rol == null) {
			return false;
		}
		for (String r : roles) {
			if (this.rol.equals(r)) {
				return true;
			}
		}
		return false;
	}
}
