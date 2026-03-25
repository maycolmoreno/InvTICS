package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando no se encuentra un equipo.
 */
public class EquipoNoEncontradoException extends RecursoNoEncontradoException {
    
    public EquipoNoEncontradoException(Integer equipoId) {
        super("Equipo", equipoId);
    }
    
    public EquipoNoEncontradoException(String mensaje) {
        super("Equipo", mensaje);
    }
}
