package com.uisrael.gestionactivosapi.dominio.valoresobjeto;

import com.uisrael.gestionactivosapi.dominio.excepciones.ValidacionNegocioException;

/**
 * Value object que representa los estados posibles de un mantenimiento.
 */
public enum EstadoMantenimiento {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En Proceso"),
    PAUSADO("Pausado"),
    COMPLETADO("Completado"),
    CANCELADO("Cancelado"),
    RECHAZADO("Rechazado"),
    POR_APROBAR("Por Aprobar");
    
    private final String descripcion;
    
    EstadoMantenimiento(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Valida si es posible transicionar a un nuevo estado.
     * 
     * @param estadoNuevo estado destino
     * @return true si la transición es válida
     */
    public boolean puedeTransicionarA(EstadoMantenimiento estadoNuevo) {
        switch (this) {
            case PENDIENTE:
                return estadoNuevo == EN_PROCESO || estadoNuevo == CANCELADO;
            case EN_PROCESO:
                return estadoNuevo == POR_APROBAR || estadoNuevo == PAUSADO || estadoNuevo == CANCELADO;
            case PAUSADO:
                return estadoNuevo == EN_PROCESO || estadoNuevo == CANCELADO;
            case POR_APROBAR:
                return estadoNuevo == COMPLETADO || estadoNuevo == RECHAZADO;
            case COMPLETADO:
                return false;
            case CANCELADO:
                return false;
            case RECHAZADO:
                return estadoNuevo == EN_PROCESO || estadoNuevo == PENDIENTE;
            default:
                return false;
        }
    }
}
