package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.Cargos;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Cargos.
 * Define el contrato para operaciones de persistencia de cargos organizacionales.
 */
public interface CargosRepositorioPuerto {
    
    /**
     * Guarda un nuevo cargo.
     * 
     * @param cargo el cargo a guardar
     * @return el cargo guardado con ID asignado
     */
    Cargos guardar(Cargos cargo);
    
    /**
     * Obtiene un cargo por su ID.
     * 
     * @param id el ID del cargo
     * @return Optional con el cargo si existe
     */
    Optional<Cargos> obtenerPorId(Integer id);
    
    /**
     * Obtiene todos los cargos.
     * 
     * @return lista de todos los cargos
     */
    List<Cargos> obtenerTodos();
    
    /**
     * Actualiza un cargo existente.
     * 
     * @param id el ID del cargo a actualizar
     * @param cargo el cargo con datos actualizados
     * @return el cargo actualizado
     */
    Cargos actualizar(int id, Cargos cargo);
    
    /**
     * Actualiza el estado de un cargo.
     * 
     * @param id el ID del cargo
     * @param cargo el cargo con el nuevo estado
     * @return el cargo con el estado actualizado
     */
    Cargos actualizarEstado(int id, Cargos cargo);
    
    /**
     * Verifica si existe un cargo con el nombre especificado.
     * 
     * @param nombre el nombre del cargo
     * @return true si existe, false en caso contrario
     */
    boolean existeNombre(String nombre);
    
    /**
     * Verifica si existe otro cargo con el nombre especificado (excluyendo el actual).
     * 
     * @param nombre el nombre del cargo
     * @param idCargo el ID del cargo actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeNombreParaOtro(String nombre, int idCargo);
    
    /**
     * Busca un cargo por su ID.
     * Alias para obtenerPorId para compatibilidad.
     * 
     * @param id el ID del cargo
     * @return Optional con el cargo si existe
     */
    default Optional<Cargos> buscarPorId(int id) {
        return obtenerPorId(id);
    }
    
    /**
     * Obtiene todos los cargos.
     * Alias para obtenerTodos para compatibilidad.
     * 
     * @return lista de todos los cargos
     */
    default List<Cargos> listarTodos() {
        return obtenerTodos();
    }
}
