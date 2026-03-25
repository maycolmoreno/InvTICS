package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.ActividadRealizada;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad ActividadRealizada.
 * Define el contrato para operaciones de persistencia de actividades completadas en mantenimiento.
 */
public interface ActividadRealizadaRepositorioPuerto {
    
    /**
     * Guarda una nueva actividad realizada.
     * 
     * @param actividad la actividad a guardar
     * @return la actividad guardada con ID asignado
     */
    ActividadRealizada guardar(ActividadRealizada actividad);
    
    /**
     * Obtiene una actividad realizada por su ID.
     * 
     * @param id el ID de la actividad
     * @return Optional con la actividad si existe
     */
    Optional<ActividadRealizada> obtenerPorId(Integer id);
    
    /**
     * Obtiene todas las actividades realizadas.
     * 
     * @return lista de todas las actividades
     */
    List<ActividadRealizada> obtenerTodas();
    
    /**
     * Actualiza una actividad realizada.
     * 
     * @param actividad la actividad con datos actualizados
     */
    void actualizar(ActividadRealizada actividad);
    
    /**
     * Elimina una actividad realizada.
     * 
     * @param id el ID de la actividad a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene actividades de un mantenimiento específico.
     * 
     * @param mantenimientoId el ID del mantenimiento
     * @return lista de actividades del mantenimiento
     */
    List<ActividadRealizada> obtenerPorMantenimiento(Integer mantenimientoId);
    
    /**
     * Obtiene actividades realizadas por un técnico específico.
     * 
     * @param tecnicoId el ID del técnico
     * @return lista de actividades realizadas por el técnico
     */
    List<ActividadRealizada> obtenerPorTecnico(Integer tecnicoId);
}
