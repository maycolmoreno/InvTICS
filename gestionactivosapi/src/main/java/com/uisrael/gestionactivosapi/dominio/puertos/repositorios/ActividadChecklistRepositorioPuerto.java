package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.ActividadChecklist;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad ActividadChecklist.
 * Define el contrato para operaciones de persistencia de plantillas de checklist.
 */
public interface ActividadChecklistRepositorioPuerto {
    
    /**
     * Guarda una nueva actividad de checklist.
     * 
     * @param actividad la actividad a guardar
     * @return la actividad guardada con ID asignado
     */
    ActividadChecklist guardar(ActividadChecklist actividad);
    
    /**
     * Obtiene una actividad de checklist por su ID.
     * 
     * @param id el ID de la actividad
     * @return Optional con la actividad si existe
     */
    Optional<ActividadChecklist> obtenerPorId(Integer id);
    
    /**
     * Obtiene todas las actividades de checklist.
     * 
     * @return lista de todas las actividades
     */
    List<ActividadChecklist> obtenerTodas();
    
    /**
     * Actualiza una actividad de checklist.
     * 
     * @param actividad la actividad con datos actualizados
     */
    void actualizar(ActividadChecklist actividad);
    
    /**
     * Elimina una actividad de checklist.
     * 
     * @param id el ID de la actividad a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene items de checklist para un mantenimiento.
     * 
     * @param mantenimientoId el ID del mantenimiento
     * @return lista de items de checklist
     */
    List<ActividadChecklist> obtenerPorMantenimiento(Integer mantenimientoId);
    
    /**
     * Obtiene items de checklist completados.
     * 
     * @param mantenimientoId el ID del mantenimiento
     * @return lista de items completados
     */
    List<ActividadChecklist> obtenerCompletados(Integer mantenimientoId);
}
