package com.uisrael.gestionactivosapi.dominio.excepciones;

public class RecepcionNoPermitidaException extends ValidacionNegocioException {

    public RecepcionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
