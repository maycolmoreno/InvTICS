package com.uisrael.gestionactivosapi.dominio.valoresobjeto;

/**
 * Value object que representa los estados posibles de un equipo.
 */
public enum EstadoEquipo {
    DISPONIBLE("Disponible"),
    ASIGNADO("Asignado"),
    MANTENIMIENTO("En Mantenimiento"),
    DEPRECIADO("Depreciado"),
    DADO_BAJA("Dado de Baja"),
    EN_REPARACION("En Reparación");
    
    private final String descripcion;
    
    EstadoEquipo(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Valida si es posible transicionar del estado actual a uno nuevo.
     * 
     * @param estadoNuevo el estado destino
     * @return true si la transición es válida
     */
    public boolean puedeTransicionarA(EstadoEquipo estadoNuevo) {
        // DISPONIBLE puede ir a: ASIGNADO, MANTENIMIENTO, DADO_BAJA
        // ASIGNADO puede ir a: DISPONIBLE, MANTENIMIENTO, EN_REPARACION
        // MANTENIMIENTO puede ir a: DISPONIBLE, ASIGNADO
        // EN_REPARACION puede ir a: DISPONIBLE, MANTENIMIENTO, DADO_BAJA
        // DEPRECIADO no puede cambiar a nada
        // DADO_BAJA no puede cambiar a nada
        
        if (this == DEPRECIADO || this == DADO_BAJA) {
            return false;
        }
        
        switch (this) {
            case DISPONIBLE:
                return estadoNuevo == ASIGNADO || estadoNuevo == MANTENIMIENTO || estadoNuevo == DADO_BAJA;
            case ASIGNADO:
                return estadoNuevo == DISPONIBLE || estadoNuevo == MANTENIMIENTO || estadoNuevo == EN_REPARACION;
            case MANTENIMIENTO:
                return estadoNuevo == DISPONIBLE || estadoNuevo == ASIGNADO || estadoNuevo == DEPRECIADO;
            case EN_REPARACION:
                return estadoNuevo == DISPONIBLE || estadoNuevo == MANTENIMIENTO || estadoNuevo == DADO_BAJA;
            default:
                return false;
        }
    }
}
