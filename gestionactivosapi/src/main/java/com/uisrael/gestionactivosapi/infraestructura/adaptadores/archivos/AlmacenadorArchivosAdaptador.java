package com.uisrael.gestionactivosapi.infraestructura.adaptadores.archivos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uisrael.gestionactivosapi.dominio.puertos.servicios.AlmacenadorArchivosPuerto;

public class AlmacenadorArchivosAdaptador implements AlmacenadorArchivosPuerto {

	private static final Logger log = LoggerFactory.getLogger(AlmacenadorArchivosAdaptador.class);

	private final Path rutaBase;

	public AlmacenadorArchivosAdaptador(String rutaBase) {
		this.rutaBase = Path.of(rutaBase);
	}

	@Override
	public String guardarArchivo(String nombreArchivo, byte[] contenido, String rutaCarpeta) {
		try {
			Path carpeta = rutaBase.resolve(rutaCarpeta);
			Files.createDirectories(carpeta);
			Path destino = carpeta.resolve(nombreArchivo);
			Files.write(destino, contenido);
			log.info("Archivo guardado: {}", destino);
			return destino.toString().replace('\\', '/');
		} catch (IOException e) {
			throw new IllegalArgumentException("No se pudo guardar el archivo: " + nombreArchivo, e);
		}
	}

	@Override
	public byte[] obtenerArchivo(String rutaArchivo) {
		try {
			Path archivo = Path.of(rutaArchivo);
			if (!Files.exists(archivo)) {
				throw new IllegalArgumentException("El archivo no existe: " + rutaArchivo);
			}
			return Files.readAllBytes(archivo);
		} catch (IOException e) {
			throw new IllegalArgumentException("No se pudo leer el archivo: " + rutaArchivo, e);
		}
	}

	@Override
	public boolean eliminarArchivo(String rutaArchivo) {
		try {
			Path archivo = Path.of(rutaArchivo);
			return Files.deleteIfExists(archivo);
		} catch (IOException e) {
			log.error("Error al eliminar archivo {}: {}", rutaArchivo, e.getMessage());
			return false;
		}
	}

	@Override
	public boolean archivoExiste(String rutaArchivo) {
		return Files.exists(Path.of(rutaArchivo));
	}

	@Override
	public String obtenerRutaBase() {
		return rutaBase.toString().replace('\\', '/');
	}
}
