package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando un equipo no está activo para realizar operaciones.
 */
public class EquipoNoActivoException extends ValidacionNegocioException {
    
    private final Integer equipoId;
    
    public EquipoNoActivoException(Integer equipoId) {
        super(String.format("Equipo %d no está activo para realizar esta operación", equipoId));
        this.equipoId = equipoId;
    }
    
    public Integer getEquipoId() {
        return equipoId;
    }
}
