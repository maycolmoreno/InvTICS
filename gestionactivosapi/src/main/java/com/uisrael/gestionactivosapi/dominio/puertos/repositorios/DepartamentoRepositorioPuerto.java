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
     * @param id el ID del Departamentos a actualizar
     * @param Departamentos el Departamentos con datos actualizados
     * @return el Departamentos actualizado
     */
    Departamentos actualizar(int id, Departamentos Departamentos);
    
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
    
    /**
     * Verifica si existe un departamento con el nombre especificado.
     * 
     * @param nombre el nombre del departamento
     * @return true si existe, false en caso contrario
     */
    boolean existeNombre(String nombre);
    
    /**
     * Verifica si existe otro departamento con el nombre especificado (excluyendo el actual).
     * 
     * @param nombre el nombre del departamento
     * @param idDepartamento el ID del departamento actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeNombreParaOtro(String nombre, int idDepartamento);
    
    /**
     * Actualiza el estado de un departamento.
     * 
     * @param id el ID del departamento
     * @param departamento el departamento con el nuevo estado
     * @return el departamento con el estado actualizado
     */
    Departamentos actualizarEstado(int id, Departamentos departamento);
    
    /**
     * Busca un departamento por su ID.
     * Alias para obtenerPorId para compatibilidad.
     * 
     * @param id el ID del departamento
     * @return Optional con el departamento si existe
     */
    default Optional<Departamentos> buscarPorId(int id) {
        return obtenerPorId(id);
    }
    
    /**
     * Obtiene todos los departamentos.
     * Alias para obtenerTodos para compatibilidad.
     * 
     * @return lista de todos los departamentos
     */
    default List<Departamentos> listarTodos() {
        return obtenerTodos();
    }
}
