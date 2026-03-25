package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Custodios.
 * Define el contrato para operaciones de persistencia de custodios (responsables de equipos).
 */
public interface CustodioRepositorioPuerto {
    
    /**
     * Guarda un nuevo Custodios.
     * 
     * @param Custodios el Custodios a guardar
     * @return el Custodios guardado con ID asignado
     */
    Custodios guardar(Custodios Custodios);
    
    /**
     * Obtiene un Custodios por su ID.
     * 
     * @param id el ID del Custodios
     * @return Optional con el Custodios si existe
     */
    Optional<Custodios> obtenerPorId(Integer id);
    
    /**
     * Obtiene todos los custodios.
     * 
     * @return lista de todos los custodios
     */
    List<Custodios> obtenerTodos();
    
    /**
     * Actualiza un Custodios existente.
     * 
     * @param Custodios el Custodios con datos actualizados
     * @return el Custodios actualizado
     */
    Custodios actualizar(int id, Custodios Custodios);
    
    /**
     * Busca un Custodios por su ID.
     * Alias para obtenerPorId para compatibilidad.
     * 
     * @param id el ID del Custodios
     * @return Optional con el Custodios si existe
     */
    default Optional<Custodios> buscarPorId(int id) {
        return obtenerPorId(id);
    }
    
    /**
     * Obtiene todos los custodios.
     * Alias para obtenerTodos para compatibilidad.
     * 
     * @return lista de todos los custodios
     */
    default List<Custodios> listarTodos() {
        return obtenerTodos();
    }
    
    /**
     * Actualiza el estado de un custodio.
     * 
     * @param id el ID del custodio
     * @param custodio el custodio con el nuevo estado
     * @return el custodio actualizado
     */
    Custodios actualizarEstado(int id, Custodios custodio);
    
    /**
     * Verifica si existe otro custodio con el correo especificado (excluyendo el actual).
     * 
     * @param correo el correo a buscar
     * @param idActual el ID del custodio actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeCorreoParaOtro(String correo, int idActual);
    
    /**
     * Verifica si existe un custodio con la cédula especificada.
     * 
     * @param cedula la cédula a buscar
     * @return true si existe, false en caso contrario
     */
    boolean existeCedula(String cedula);
    
    /**
     * Verifica si existe otro custodio con la cédula especificada (excluyendo el actual).
     * 
     * @param cedula la cédula a buscar
     * @param idActual el ID del custodio actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeCedulaParaOtro(String cedula, int idActual);
    
    /**
     * Vincula un usuario con un custodio.
     * 
     * @param idCustodio el ID del custodio
     * @param idUsuario el ID del usuario
     * @return el custodio actualizado
     */
    Custodios vincularUsuario(int idCustodio, int idUsuario);
    
    /**
     * Verifica si un usuario está vinculado con otro custodio.
     * 
     * @param idUsuario el ID del usuario
     * @param idCustodioActual el ID del custodio actual
     * @return true si el usuario está vinculado a otro custodio
     */
    boolean existeUsuarioVinculadoEnOtroCustodio(int idUsuario, int idCustodioActual);
    
    /**
     * Elimina un Custodios.
     * 
     * @param id el ID del Custodios a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene custodios por estado.
     * 
     * @param estado true para activos, false para inactivos
     * @return lista de custodios en el estado especificado
     */
    List<Custodios> obtenerPorEstado(boolean estado);
    
    /**
     * Obtiene un Custodios por su cédula/identificación.
     * 
     * @param cedula la cédula del Custodios
     * @return Optional con el Custodios si existe
     */
    Optional<Custodios> obtenerPorCedula(String cedula);
    
    /**
     * Obtiene custodios por Departamentos.
     * 
     * @param departamentoId el ID del Departamentos
     * @return lista de custodios del Departamentos
     */
    List<Custodios> obtenerPorDepartamento(Integer departamentoId);
    
    /**
     * Obtiene custodios autorizados para recibir equipos de cierta categoría.
     * 
     * @param categoriaId el ID de la categoría
     * @return lista de custodios autorizados
     */
    List<Custodios> obtenerAutorizadosPorCategoria(Integer categoriaId);
}
