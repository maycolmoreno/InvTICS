package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Usuarios.
 * Define el contrato para operaciones de persistencia de usuarios del sistema.
 */
public interface UsuarioRepositorioPuerto {
    
    /**
     * Guarda un nuevo Usuarios.
     * 
     * @param Usuarios el Usuarios a guardar
     * @return el Usuarios guardado con ID asignado
     */
    Usuarios guardar(Usuarios Usuarios);
    
    /**
     * Obtiene un Usuarios por su ID.
     * 
     * @param id el ID del Usuarios
     * @return Optional con el Usuarios si existe
     */
    Optional<Usuarios> obtenerPorId(Integer id);
    
    /**
     * Obtiene todos los usuarios.
     * 
     * @return lista de todos los usuarios
     */
    List<Usuarios> obtenerTodos();
    
    /**
     * Actualiza un Usuarios existente.
     * 
     * @param Usuarios el Usuarios con datos actualizados
     */
    void actualizar(Usuarios Usuarios);
    
    /**
     * Elimina un Usuarios.
     * 
     * @param id el ID del Usuarios a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene un Usuarios por su nombre de Usuarios.
     * 
     * @param nombreUsuario el nombre de Usuarios
     * @return Optional con el Usuarios si existe
     */
    Optional<Usuarios> obtenerPorNombreUsuario(String nombreUsuario);
    
    /**
     * Obtiene un Usuarios por su correo electrónico.
     * 
     * @param correo el correo electrónico
     * @return Optional con el Usuarios si existe
     */
    Optional<Usuarios> obtenerPorCorreo(String correo);
    
    /**
     * Obtiene usuarios activos.
     * 
     * @return lista de usuarios activos
     */
    List<Usuarios> obtenerActivos();
    
    /**
     * Obtiene usuarios con un Roles específico.
     * 
     * @param rolId el ID del Roles
     * @return lista de usuarios con el Roles especificado
     */
    List<Usuarios> obtenerPorRol(Integer rolId);
    
    /**
     * Verifica si existe un Usuarios con el correo especificado.
     * 
     * @param correo el correo a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existePorCorreo(String correo);
}
