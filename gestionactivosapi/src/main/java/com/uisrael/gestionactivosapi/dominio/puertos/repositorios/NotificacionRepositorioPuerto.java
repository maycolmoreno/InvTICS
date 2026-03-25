package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.Notificacion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Notificacion.
 * Define el contrato para operaciones de persistencia de notificaciones.
 */
public interface NotificacionRepositorioPuerto {
    
    /**
     * Guarda una nueva notificación.
     * 
     * @param notificacion la notificación a guardar
     * @return la notificación guardada con ID asignado
     */
    Notificacion guardar(Notificacion notificacion);
    
    /**
     * Obtiene una notificación por su ID.
     * 
     * @param id el ID de la notificación
     * @return Optional con la notificación si existe
     */
    Optional<Notificacion> obtenerPorId(Integer id);
    
    /**
     * Obtiene todas las notificaciones.
     * 
     * @return lista de todas las notificaciones
     */
    List<Notificacion> obtenerTodas();
    
    /**
     * Actualiza una notificación existente.
     * 
     * @param notificacion la notificación con datos actualizados
     */
    void actualizar(Notificacion notificacion);
    
    /**
     * Elimina una notificación.
     * 
     * @param id el ID de la notificación a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene notificaciones pendientes de envío para un Usuarios.
     * 
     * @param usuarioId el ID del Usuarios
     * @return lista de notificaciones pendientes
     */
    List<Notificacion> obtenerPendientesPorUsuario(Integer usuarioId);
    
    /**
     * Obtiene notificaciones enviadas en un rango de fechas.
     * 
     * @param desde fecha de inicio
     * @param hasta fecha de fin
     * @return lista de notificaciones en el rango
     */
    List<Notificacion> obtenerPorRangoFechas(LocalDateTime desde, LocalDateTime hasta);
    
    /**
     * Obtiene notificaciones por tipo.
     * 
     * @param tipo el tipo de notificación
     * @return lista de notificaciones del tipo especificado
     */
    List<Notificacion> obtenerPorTipo(String tipo);
    
    /**
     * Marcas una notificación como enviada.
     * 
     * @param id el ID de la notificación
     */
    void marcarComoEnviada(Integer id);
}
