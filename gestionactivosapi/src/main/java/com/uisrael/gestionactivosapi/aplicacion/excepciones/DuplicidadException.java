package com.uisrael.gestionactivosapi.aplicacion.excepciones;

public class DuplicidadException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DuplicidadException(String message) {
		super(message);
	}
}
