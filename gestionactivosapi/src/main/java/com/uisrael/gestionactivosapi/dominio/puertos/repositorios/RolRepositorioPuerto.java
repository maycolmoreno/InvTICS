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
     * @param Roles el Roles con datos actualizados
     */
    void actualizar(Roles Roles);
    
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
}
