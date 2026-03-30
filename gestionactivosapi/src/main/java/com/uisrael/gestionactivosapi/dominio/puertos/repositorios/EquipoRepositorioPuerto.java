package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Equipos.
 * Define el contrato para operaciones de persistencia de equipos.
 * 
 * Sin dependencias a Spring ni JPA.
 * Las entidades usadas son del dominio, no JPA.
 */
public interface EquipoRepositorioPuerto {
    
    /**
     * Guarda un nuevo equipo o actualiza uno existente.
     * 
     * @param equipo el equipo a guardar
     * @return el equipo guardado con ID asignado
     * @throws IllegalArgumentException si el equipo es nulo
     */
    Equipos guardar(Equipos equipo);
    
    /**
     * Obtiene un equipo por su ID.
     * 
     * @param id el ID del equipo
     * @return Optional con el equipo si existe
     */
    Optional<Equipos> obtenerPorId(Integer id);
    
    /**
     * Obtiene todos los equipos.
     * 
     * @return lista de todos los equipos
     */
    List<Equipos> obtenerTodos();
    
    /**
     * Actualiza un equipo existente.
     * 
     * @param equipo el equipo con datos actualizados
     * @throws IllegalArgumentException si el equipo no existe
     */
    void actualizar(Equipos equipo);
    
    /**
     * Elimina un equipo por su ID.
     * 
     * @param id el ID del equipo a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene equipos filtrados por estado.
     * 
     * @param estado true para activos, false para inactivos
     * @return lista de equipos en el estado especificado
     */
    List<Equipos> obtenerPorEstado(boolean estado);
    
    /**
     * Obtiene equipos asignados a un Custodios específico.
     * 
     * @param custodioId el ID del Custodios
     * @return lista de equipos asignados al Custodios
     */
    List<Equipos> obtenerPorCustodio(Integer custodioId);
    
    /**
     * Obtiene equipos por categoría.
     * 
     * @param categoriaId el ID de la categoría
     * @return lista de equipos en la categoría
     */
    List<Equipos> obtenerPorCategoria(Integer categoriaId);
    
    /**
     * Busca un equipo por su ID.
     * Alias para obtenerPorId para compatibilidad.
     * 
     * @param id el ID del equipo
     * @return Optional con el equipo si existe
     */
    default Optional<Equipos> buscarPorId(int id) {
        return obtenerPorId(id);
    }
    
    /**
     * Obtiene todos los equipos.
     * Alias para obtenerTodos para compatibilidad.
     * 
     * @return lista de todos los equipos
     */
    default List<Equipos> listarTodos() {
        return obtenerTodos();
    }
    
    /**
     * Actualiza un equipo existente con ID especificado.
     * 
     * @param id el ID del equipo a actualizar
     * @param equipo el equipo con datos actualizados
     * @return el equipo actualizado
     */
    Equipos actualizar(int id, Equipos equipo);
    
    /**
     * Verifica si existe un equipo con el Código SAP (Activo Fijo) especificado.
     * 
     * @param codigo el código SAP
     * @return true si existe, false en caso contrario
     */
    boolean existeCodigo(String codigo);
    
    /**
     * Verifica si existe otro equipo con el Código SAP especificado (excluyendo el actual).
     * 
     * @param codigo el código SAP
     * @param idEquipo el ID del equipo actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeCodigoParaOtro(String codigo, int idEquipo);
    
    /**
     * Verifica si existe un equipo con el serial especificado.
     * 
     * @param serial el serial del equipo
     * @return true si existe, false en caso contrario
     */
    boolean existeSerial(String serial);
    
    /**
     * Verifica si existe otro equipo con el serial especificado (excluyendo el actual).
     * 
     * @param serial el serial del equipo
     * @param idEquipo el ID del equipo actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeSerialParaOtro(String serial, int idEquipo);
    
    /**
     * Verifica si existe un equipo con la dirección IP especificada.
     * 
     * @param ip la dirección IP
     * @return true si existe, false en caso contrario
     */
    boolean existeIP(String ip);
    
    /**
     * Verifica si existe otro equipo con la dirección IP especificada (excluyendo el actual).
     * 
     * @param ip la dirección IP
     * @param idEquipo el ID del equipo actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeIPParaOtro(String ip, int idEquipo);
    
    /**
     * Verifica si existe un equipo con la dirección MAC especificada.
     * 
     * @param mac la dirección MAC
     * @return true si existe, false en caso contrario
     */
    boolean existeMAC(String mac);
    
    /**
     * Verifica si existe otro equipo con la dirección MAC especificada (excluyendo el actual).
     * 
     * @param mac la dirección MAC
     * @param idEquipo el ID del equipo actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeMACParaOtro(String mac, int idEquipo);

    Equipos actualizarEstado(int id, boolean estado);
}
