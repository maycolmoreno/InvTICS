package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando se intenta una transición de estado inválida de un equipo.
 */
public class TransicionEstadoIlegalException extends ValidacionNegocioException {
    
    private final String estadoActual;
    private final String estadoIntentado;
    
    public TransicionEstadoIlegalException(String estadoActual, String estadoIntentado) {
        super(String.format("No se puede transicionar de '%s' a '%s'", estadoActual, estadoIntentado));
        this.estadoActual = estadoActual;
        this.estadoIntentado = estadoIntentado;
    }
    
    public String getEstadoActual() {
        return estadoActual;
    }
    
    public String getEstadoIntentado() {
        return estadoIntentado;
    }
}
