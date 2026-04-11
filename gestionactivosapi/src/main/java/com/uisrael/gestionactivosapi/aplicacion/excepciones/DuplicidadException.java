package com.uisrael.gestionactivosapi.aplicacion.excepciones;

import com.uisrael.gestionactivosapi.dominio.excepciones.ExcepcionDominio;

public class DuplicidadException extends ExcepcionDominio {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicidadException(String message) {
		super(message);
	}
}
