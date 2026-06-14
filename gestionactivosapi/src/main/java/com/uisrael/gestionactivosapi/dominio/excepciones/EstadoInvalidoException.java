package com.uisrael.gestionactivosapi.dominio.excepciones;

public class EstadoInvalidoException extends ValidacionNegocioException {

    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
