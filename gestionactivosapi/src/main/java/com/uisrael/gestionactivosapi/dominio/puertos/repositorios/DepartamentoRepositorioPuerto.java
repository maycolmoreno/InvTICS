package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Departamentos.
 * Define el contrato para operaciones de persistencia de departamentos organizacionales.
 */
public interface DepartamentoRepositorioPuerto {
    
    /**
     * Guarda un nuevo Departamentos.
     * 
     * @param Departamentos el Departamentos a guardar
     * @return el Departamentos guardado con ID asignado
     */
    Departamentos guardar(Departamentos Departamentos);
    
    /**
     * Obtiene un Departamentos por su ID.
     * 
     * @param id el ID del Departamentos
     * @return Optional con el Departamentos si existe
     */
    Optional<Departamentos> obtenerPorId(Integer id);
    
    /**
     * Obtiene todos los departamentos.
     * 
     * @return lista de todos los departamentos
     */
    List<Departamentos> obtenerTodos();
    
    /**
     * Actualiza un Departamentos existente.
     * 
     * @param Departamentos el Departamentos con datos actualizados
     */
    void actualizar(Departamentos Departamentos);
    
    /**
     * Elimina un Departamentos.
     * 
     * @param id el ID del Departamentos a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene un Departamentos por su nombre.
     * 
     * @param nombre el nombre del Departamentos
     * @return Optional con el Departamentos si existe
     */
    Optional<Departamentos> obtenerPorNombre(String nombre);
    
    /**
     * Obtiene departamentos activos.
     * 
     * @return lista de departamentos activos
     */
    List<Departamentos> obtenerActivos();
}
