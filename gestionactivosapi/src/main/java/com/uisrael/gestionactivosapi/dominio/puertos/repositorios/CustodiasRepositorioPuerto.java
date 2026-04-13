package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.dominio.modelo.Pagina;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Custodias.
 * Define el contrato para operaciones de persistencia de custodias (asignaciones de equipos a custodios).
 */
public interface CustodiasRepositorioPuerto {
    
    /**
     * Guarda una nueva custodia.
     * 
     * @param custodia la custodia a guardar
     * @return la custodia guardada con ID asignado
     */
    Custodias guardar(Custodias custodia);
    
    /**
     * Obtiene una custodia por su ID.
     * 
     * @param id el ID de la custodia
     * @return Optional con la custodia si existe
     */
    Optional<Custodias> obtenerPorId(Integer id);
    
    /**
     * Obtiene todas las custodias.
     * 
     * @return lista de todas las custodias
     */
    List<Custodias> obtenerTodos();
    
    /**
     * Actualiza una custodia existente.
     * 
     * @param id el ID de la custodia a actualizar
     * @param custodia la custodia con datos actualizados
     * @return la custodia actualizada
     */
    Custodias actualizar(int id, Custodias custodia);
    
    /**
     * Actualiza el estado de una custodia.
     * 
     * @param id el ID de la custodia
     * @param custodia la custodia con el nuevo estado
     * @return la custodia con el estado actualizado
     */
    Custodias actualizarEstado(int id, Custodias custodia);
    
    /**
     * Verifica si existe una custodia activa para un equipo.
     * 
     * @param idEquipo el ID del equipo
     * @return true si existe una custodia activa, false en caso contrario
     */
    boolean existeCustodiaActivaPorEquipo(int idEquipo);
    
    /**
     * Verifica si existe una custodia activa para un equipo excluyendo un registro específico.
     * 
     * @param idEquipo el ID del equipo
     * @param idCustodiaEquipo el ID de la custodia a excluir
     * @return true si existe otra custodia activa, false en caso contrario
     */
    boolean existeCustodiaActivaPorEquipoParaOtroRegistro(int idEquipo, int idCustodiaEquipo);
    
    /**
     * Cuenta custodias por tipo de movimiento.
     * 
     * @param tipoMovimiento el tipo de movimiento
     * @return cantidad de custodias del tipo especificado
     */
    long contarPorTipoMovimiento(String tipoMovimiento);
    
    /**
     * Busca la custodia activa para un equipo.
     * 
     * @param idEquipo el ID del equipo
     * @return Optional con la custodia activa si existe
     */
    Optional<Custodias> buscarActivaPorEquipo(int idEquipo);
    
    /**
     * Busca una custodia por su ID.
     * Alias para obtenerPorId para compatibilidad.
     * 
     * @param id el ID de la custodia
     * @return Optional con la custodia si existe
     */
    default Optional<Custodias> buscarPorId(int id) {
        return obtenerPorId(id);
    }
    
    /**
     * Obtiene todas las custodias.
     * Alias para obtenerTodos para compatibilidad.
     * 
     * @return lista de todas las custodias
     */
    default List<Custodias> listarTodos() {
        return obtenerTodos();
    }

    Pagina<Custodias> listarPaginado(int pagina, int tamanio);

    List<Custodias> buscarPorGrupoActa(int idCustodio, String tipoMovimiento, java.time.LocalDate fechaInicio);
}
