package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Ubicaciones.
 * Define el contrato para operaciones de persistencia de ubicaciones geográficas.
 */
public interface UbicacionRepositorioPuerto {
    
    /**
     * Guarda una nueva ubicación.
     * 
     * @param ubicacion la ubicación a guardar
     * @return la ubicación guardada con ID asignado
     */
    Ubicaciones guardar(Ubicaciones ubicacion);
    
    /**
     * Obtiene una ubicación por su ID.
     * 
     * @param id el ID de la ubicación
     * @return Optional con la ubicación si existe
     */
    Optional<Ubicaciones> obtenerPorId(Integer id);
    
    /**
     * Obtiene todas las ubicaciones.
     * 
     * @return lista de todas las ubicaciones
     */
    List<Ubicaciones> obtenerTodos();
    
    /**
     * Actualiza una ubicación existente.
     * 
     * @param id el ID de la ubicación a actualizar
     * @param ubicacion la ubicación con datos actualizados
     * @return la ubicación actualizada
     */
    Ubicaciones actualizar(int id, Ubicaciones ubicacion);
    
    /**
     * Actualiza el estado de una ubicación.
     * 
     * @param id el ID de la ubicación
     * @param ubicacion la ubicación con el nuevo estado
     * @return la ubicación con el estado actualizado
     */
    Ubicaciones actualizarEstado(int id, Ubicaciones ubicacion);
    
    /**
     * Verifica si existe una ubicación con el nombre especificado.
     * 
     * @param nombre el nombre de la ubicación
     * @return true si existe, false en caso contrario
     */
    boolean existeNombre(String nombre);
    
    /**
     * Verifica si existe otra ubicación con el nombre especificado (excluyendo la actual).
     * 
     * @param nombre el nombre de la ubicación
     * @param idUbicacion el ID de la ubicación actual a excluir
     * @return true si existe otra, false en caso contrario
     */
    boolean existeNombreParaOtro(String nombre, int idUbicacion);
    
    /**
     * Busca una ubicación por su ID.
     * Alias para obtenerPorId para compatibilidad.
     * 
     * @param id el ID de la ubicación
     * @return Optional con la ubicación si existe
     */
    default Optional<Ubicaciones> buscarPorId(int id) {
        return obtenerPorId(id);
    }
    
    /**
     * Obtiene todas las ubicaciones.
     * Alias para obtenerTodos para compatibilidad.
     * 
     * @return lista de todas las ubicaciones
     */
    default List<Ubicaciones> listarTodos() {
        return obtenerTodos();
    }
}
