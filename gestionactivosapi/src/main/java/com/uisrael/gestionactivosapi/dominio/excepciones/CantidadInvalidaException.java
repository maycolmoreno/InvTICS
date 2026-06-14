package com.uisrael.gestionactivosapi.dominio.excepciones;

public class CantidadInvalidaException extends ValidacionNegocioException {

    public CantidadInvalidaException(String mensaje) {
        super(mensaje);
    }
}
