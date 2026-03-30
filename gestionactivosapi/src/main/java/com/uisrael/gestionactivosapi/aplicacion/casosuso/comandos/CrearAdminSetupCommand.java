package com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos;

public record CrearAdminSetupCommand(
		String nombre,
		String correo,
		String contrasena,
		String cedula) {
}
