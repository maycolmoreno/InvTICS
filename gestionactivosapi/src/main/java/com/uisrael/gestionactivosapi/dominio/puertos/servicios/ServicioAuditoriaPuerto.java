package com.uisrael.gestionactivosapi.dominio.puertos.servicios;

/**
 * Puerto para el registro de auditoría y trazabilidad de operaciones.
 * Define el contrato para logging de acciones del dominio sin dependencias concretas.
 */
public interface ServicioAuditoriaPuerto {
    
    /**
     * Registra una acción realizada en el sistema.
     * 
     * @param usuarioId ID del usuario que realizó la acción
     * @param tipoAccion tipo de acción (CREAR, ACTUALIZAR, ELIMINAR, etc)
     * @param entidad nombre de la entidad afectada
     * @param descripcion descripción detallada de la acción
     * @param datosAnteriores datos previos (en caso de actualización)
     * @param datosNuevos datos nuevos (en caso de creación/actualización)
     */
    void registrarAccion(Integer usuarioId, String tipoAccion, String entidad,
            String descripcion, Object datosAnteriores, Object datosNuevos);
    
    /**
     * Registra un evento de error en el sistema.
     * 
     * @param usuarioId ID del usuario (puede ser nulo)
     * @param codigoError código del error
     * @param descripcionError descripción del error
     * @param stackTrace stack trace de la excepción
     */
    void registrarError(Integer usuarioId, String codigoError, String descripcionError, String stackTrace);
    
    /**
     * Registra acceso a recursos sensibles.
     * 
     * @param usuarioId ID del usuario
     * @param recurso recurso accedido
     * @param tipo tipo de acceso (LECTURA, ESCRITURA, ELIMINACIÓN)
     */
    void registrarAccesoRecurso(Integer usuarioId, String recurso, String tipo);
    
    /**
     * Obtiene el historial de auditoría para una entidad específica.
     * 
     * @param entidad nombre de la entidad
     * @param entityId ID de la instancia de la entidad
     * @return lista con el historial
     */
    java.util.List<java.util.Map<String, Object>> obtenerHistorial(String entidad, Integer entityId);
    
    /**
     * Purga registros de auditoría antiguos (para mantenimiento).
     * 
     * @param diasRetencio días de retención mínima
     * @return número de registros eliminados
     */
    Integer purgarAuditoriasAntiguas(Integer diasRetencio);
}
