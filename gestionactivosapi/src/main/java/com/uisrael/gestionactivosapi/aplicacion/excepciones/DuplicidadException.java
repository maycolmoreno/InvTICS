package com.uisrael.gestionactivosapi.aplicacion.excepciones;

import com.uisrael.gestionactivosapi.dominio.excepciones.ExcepcionDominio;

public class DuplicidadException extends ExcepcionDominio {
	public DuplicidadException(String message) {
		super(message);
	}
}
