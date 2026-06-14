package com.uisrael.gestionactivosapi.dominio.excepciones;

public class CantidadExcedidaException extends ValidacionNegocioException {

    public CantidadExcedidaException(String mensaje) {
        super(mensaje);
    }
}
