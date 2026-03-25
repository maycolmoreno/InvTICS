package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Marcas.
 * Define el contrato para operaciones de persistencia de marcas de equipos.
 */
public interface MarcaRepositorioPuerto {
    
    /**
     * Guarda una nueva Marcas.
     * 
     * @param Marcas la Marcas a guardar
     * @return la Marcas guardada con ID asignado
     */
    Marcas guardar(Marcas Marcas);
    
    /**
     * Obtiene una Marcas por su ID.
     * 
     * @param id el ID de la Marcas
     * @return Optional con la Marcas si existe
     */
    Optional<Marcas> obtenerPorId(Integer id);
    
    /**
     * Obtiene todas las marcas.
     * 
     * @return lista de todas las marcas
     */
    List<Marcas> obtenerTodas();
    
    /**
     * Actualiza una Marcas existente.
     * 
     * @param Marcas la Marcas con datos actualizados
     */
    void actualizar(Marcas Marcas);
    
    /**
     * Elimina una Marcas.
     * 
     * @param id el ID de la Marcas a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene una Marcas por su nombre.
     * 
     * @param nombre el nombre de la Marcas
     * @return Optional con la Marcas si existe
     */
    Optional<Marcas> obtenerPorNombre(String nombre);
    
    /**
     * Obtiene marcas activas.
     * 
     * @return lista de marcas activas
     */
    List<Marcas> obtenerActivas();
}
