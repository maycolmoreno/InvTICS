package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando un mantenimiento no puede ser modificado en su estado actual.
 */
public class MantenimientoNoModificableException extends ValidacionNegocioException {
    
    private final Integer mantenimientoId;
    private final String estadoActual;
    
    public MantenimientoNoModificableException(Integer mantenimientoId, String estadoActual) {
        super(String.format("Mantenimiento %d no puede ser modificado en estado '%s'", 
            mantenimientoId, estadoActual));
        this.mantenimientoId = mantenimientoId;
        this.estadoActual = estadoActual;
    }
    
    public Integer getMantenimientoId() {
        return mantenimientoId;
    }
    
    public String getEstadoActual() {
        return estadoActual;
    }
}
