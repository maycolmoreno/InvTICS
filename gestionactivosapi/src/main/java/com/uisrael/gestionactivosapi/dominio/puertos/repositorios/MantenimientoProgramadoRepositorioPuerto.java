package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.MantenimientoProgramado;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad MantenimientoProgramado.
 * Define el contrato para operaciones de persistencia de mantenimientos programados.
 */
public interface MantenimientoProgramadoRepositorioPuerto {
    
    /**
     * Guarda un nuevo mantenimiento programado.
     * 
     * @param mantenimiento el mantenimiento a guardar
     * @return el mantenimiento guardado con ID asignado
     */
    MantenimientoProgramado guardar(MantenimientoProgramado mantenimiento);
    
    /**
     * Obtiene un mantenimiento programado por su ID.
     * 
     * @param id el ID del mantenimiento
     * @return Optional con el mantenimiento si existe
     */
    Optional<MantenimientoProgramado> obtenerPorId(Integer id);
    
    /**
     * Obtiene todos los mantenimientos programados.
     * 
     * @return lista de todos los mantenimientos
     */
    List<MantenimientoProgramado> obtenerTodos();
    
    /**
     * Actualiza un mantenimiento programado.
     * 
     * @param mantenimiento el mantenimiento con datos actualizados
     */
    void actualizar(MantenimientoProgramado mantenimiento);
    
    /**
     * Elimina un mantenimiento programado.
     * 
     * @param id el ID del mantenimiento a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene mantenimientos programados próximos a vencer.
     * 
     * @param dias número de días para considerar próximo
     * @return lista de mantenimientos próximos
     */
    List<MantenimientoProgramado> obtenerProximos(Integer dias);
    
    /**
     * Obtiene mantenimientos vencidos sin ejecutarse.
     * 
     * @return lista de mantenimientos vencidos
     */
    List<MantenimientoProgramado> obtenerVencidos();
    
    /**
     * Obtiene mantenimientos programados para un equipo.
     * 
     * @param equipoId el ID del equipo
     * @return lista de mantenimientos programados
     */
    List<MantenimientoProgramado> obtenerPorEquipo(Integer equipoId);
    
    /**
     * Obtiene mantenimientos en un rango de fechas.
     * 
     * @param desde fecha de inicio
     * @param hasta fecha de fin
     * @return lista de mantenimientos en el rango
     */
    List<MantenimientoProgramado> obtenerPorRangoFechas(LocalDateTime desde, LocalDateTime hasta);
}
