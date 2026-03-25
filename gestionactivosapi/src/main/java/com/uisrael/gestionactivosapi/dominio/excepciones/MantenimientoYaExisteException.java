package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando un mantenimiento ya existe y no se puede crear uno duplicado.
 */
public class MantenimientoYaExisteException extends ValidacionNegocioException {
    
    public MantenimientoYaExisteException(Integer equipoId, String tipoMantenimiento) {
        super(String.format("Ya existe un mantenimiento '%s' en proceso para el equipo %d", 
            tipoMantenimiento, equipoId));
    }
    
    public MantenimientoYaExisteException(String mensaje) {
        super(mensaje);
    }
}
