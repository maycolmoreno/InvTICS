package com.uisrael.gestionactivosapi.infraestructura.servicios.modelo;

public record ImagenMantenimientoComando(
		String nombreArchivo,
		String rutaArchivo,
		Long tamanioBytes) {
}
