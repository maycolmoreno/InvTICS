package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando una actividad ya ha sido completada y no puede ser modificada.
 */
public class ActividadYaCompletadaException extends ValidacionNegocioException {
    
    private final Integer actividadId;
    
    public ActividadYaCompletadaException(Integer actividadId) {
        super(String.format("Actividad %d ya ha sido completada y no puede ser modificada", actividadId));
        this.actividadId = actividadId;
    }
    
    public Integer getActividadId() {
        return actividadId;
    }
}
