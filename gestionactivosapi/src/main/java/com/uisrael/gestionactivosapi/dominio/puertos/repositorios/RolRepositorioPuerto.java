package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.Roles;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Roles.
 * Define el contrato para operaciones de persistencia de roles de Usuarios.
 */
public interface RolRepositorioPuerto {
    
    /**
     * Guarda un nuevo Roles.
     * 
     * @param Roles el Roles a guardar
     * @return el Roles guardado con ID asignado
     */
    Roles guardar(Roles Roles);
    
    /**
     * Obtiene un Roles por su ID.
     * 
     * @param id el ID del Roles
     * @return Optional con el Roles si existe
     */
    Optional<Roles> obtenerPorId(Integer id);
    
    /**
     * Obtiene todos los roles.
     * 
     * @return lista de todos los roles
     */
    List<Roles> obtenerTodos();
    
    /**
     * Actualiza un Roles existente.
     * 
     * @param id el ID del Roles a actualizar
     * @param Roles el Roles con datos actualizados
     * @return el Roles actualizado
     */
    Roles actualizar(int id, Roles Roles);
    
    /**
     * Elimina un Roles.
     * 
     * @param id el ID del Roles a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene un Roles por su nombre.
     * 
     * @param nombre el nombre del Roles
     * @return Optional con el Roles si existe
     */
    Optional<Roles> obtenerPorNombre(String nombre);
    
    /**
     * Obtiene roles activos.
     * 
     * @return lista de roles activos
     */
    List<Roles> obtenerActivos();
    
    /**
     * Busca un rol por su nombre.
     * Alias para obtenerPorNombre para compatibilidad.
     * 
     * @param nombre el nombre del rol
     * @return Optional con el rol si existe
     */
    default Optional<Roles> buscarPorNombre(String nombre) {
        return obtenerPorNombre(nombre);
    }
    
    /**
     * Busca un rol por su ID.
     * Alias para obtenerPorId para compatibilidad.
     * 
     * @param id el ID del rol
     * @return Optional con el rol si existe
     */
    default Optional<Roles> buscarPorId(int id) {
        return obtenerPorId(id);
    }
    
    /**
     * Obtiene todos los roles.
     * Alias para obtenerTodos para compatibilidad.
     * 
     * @return lista de todos los roles
     */
    default List<Roles> listarTodos() {
        return obtenerTodos();
    }
}
