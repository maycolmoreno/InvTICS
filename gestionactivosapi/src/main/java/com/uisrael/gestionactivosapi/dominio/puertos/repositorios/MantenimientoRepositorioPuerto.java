package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Mantenimiento.
 * Define el contrato para operaciones de persistencia de registros de mantenimiento.
 */
public interface MantenimientoRepositorioPuerto {
    
    /**
     * Guarda un nuevo registro de mantenimiento.
     * 
     * @param mantenimiento el registro de mantenimiento a guardar
     * @return el mantenimiento guardado con ID asignado
     */
    Mantenimientos guardar(Mantenimientos mantenimiento);
    
    /**
     * Obtiene un mantenimiento por su ID.
     * 
     * @param id el ID del mantenimiento
     * @return Optional con el mantenimiento si existe
     */
    Optional<Mantenimientos> obtenerPorId(Integer id);
    
    /**
     * Obtiene todos los registros de mantenimiento.
     * 
     * @return lista de todos los mantenimientos
     */
    List<Mantenimientos> obtenerTodos();
    
    /**
     * Actualiza un registro de mantenimiento existente.
     * 
     * @param mantenimiento el mantenimiento con datos actualizados
     * @return el mantenimiento actualizado
     */
    Mantenimientos actualizar(Mantenimientos mantenimiento);
    
    /**
     * Busca un mantenimiento por su ID.
     * Alias para obtenerPorId para compatibilidad.
     * 
     * @param id el ID del mantenimiento
     * @return Optional con el mantenimiento si existe
     */
    default Optional<Mantenimientos> buscarPorId(int id) {
        return obtenerPorId(id);
    }
    
    /**
     * Obtiene todos los registros de mantenimiento.
     * Alias para obtenerTodos para compatibilidad.
     * 
     * @return lista de todos los mantenimientos
     */
    default List<Mantenimientos> listarTodos() {
        return obtenerTodos();
    }
    
    /**
     * Obtiene la máxima secuencia de mantenimiento para un año específico.
     * 
     * @param year el año
     * @return el máximo número de secuencia o null si no hay registros
     */
    Integer obtenerMaxSecuenciaPorYear(int year);
    
    /**
     * Obtiene el último cierre de mantenimiento para un equipo.
     * 
     * @param equipoId el ID del equipo
     * @return LocalDateTime del último cierre o null si no hay registros
     */
    LocalDateTime obtenerUltimoCierrePorEquipo(int equipoId);
    
    /**
     * Elimina un registro de mantenimiento.
     * 
     * @param id el ID del mantenimiento a eliminar
     */
    void eliminar(int id);

    List<Mantenimientos> obtenerPorEquipo(int equipoId);

    default List<Mantenimientos> obtenerPorEstado(String estado) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    default List<Mantenimientos> obtenerPendientes() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    default List<Mantenimientos> obtenerPorRangoFechas(LocalDateTime desde, LocalDateTime hasta) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    List<Mantenimientos> guardarTodos(List<Mantenimientos> mantenimientos);
}
