package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.CategoriaEquipos;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Categoria.
 * Define el contrato para operaciones de persistencia de categorías de equipos.
 */
public interface CategoriaRepositorioPuerto {
    
    /**
     * Guarda una nueva categoría.
     * 
     * @param categoria la categoría a guardar
     * @return la categoría guardada con ID asignado
     */
    CategoriaEquipos guardar(CategoriaEquipos categoria);
    
    /**
     * Obtiene una categoría por su ID.
     * 
     * @param id el ID de la categoría
     * @return Optional con la categoría si existe
     */
    Optional<CategoriaEquipos> obtenerPorId(Integer id);
    
    /**
     * Obtiene todas las categorías.
     * 
     * @return lista de todas las categorías
     */
    List<CategoriaEquipos> obtenerTodas();
    
    /**
     * Actualiza una categoría existente.
     * 
     * @param categoria la categoría con datos actualizados
     * @return la categoría actualizada
     */
    CategoriaEquipos actualizar(CategoriaEquipos categoria);
    
    /**
     * Busca una categoría por su ID.
     * Alias para obtenerPorId para compatibilidad.
     * 
     * @param id el ID de la categoría
     * @return Optional con la categoría si existe
     */
    default Optional<CategoriaEquipos> buscarPorId(int id) {
        return obtenerPorId(id);
    }
    
    /**
     * Obtiene todas las categorías.
     * Alias para obtenerTodas para compatibilidad.
     * 
     * @return lista de todas las categorías
     */
    default List<CategoriaEquipos> listarTodos() {
        return obtenerTodas();
    }
    
    /**
     * Elimina una categoría.
     * 
     * @param id el ID de la categoría a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene una categoría por su nombre.
     * 
     * @param nombre el nombre de la categoría
     * @return Optional con la categoría si existe
     */
    Optional<CategoriaEquipos> obtenerPorNombre(String nombre);
    
    /**
     * Obtiene categorías activas.
     * 
     * @return lista de categorías activas
     */
    List<CategoriaEquipos> obtenerActivas();
}
