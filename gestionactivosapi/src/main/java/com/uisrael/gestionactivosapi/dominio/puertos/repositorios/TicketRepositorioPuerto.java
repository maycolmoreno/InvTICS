package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.Ticket;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad Ticket.
 * Define el contrato para operaciones de persistencia de tickets de soporte/mantenimiento.
 */
public interface TicketRepositorioPuerto {
    
    /**
     * Guarda un nuevo ticket.
     * 
     * @param ticket el ticket a guardar
     * @return el ticket guardado con ID asignado
     */
    Ticket guardar(Ticket ticket);
    
    /**
     * Obtiene un ticket por su ID.
     * 
     * @param id el ID del ticket
     * @return Optional con el ticket si existe
     */
    Optional<Ticket> obtenerPorId(Integer id);
    
    /**
     * Obtiene todos los tickets.
     * 
     * @return lista de todos los tickets
     */
    List<Ticket> obtenerTodos();
    
    /**
     * Actualiza un ticket existente.
     * 
     * @param ticket el ticket con datos actualizados
     */
    void actualizar(Ticket ticket);
    
    /**
     * Elimina un ticket.
     * 
     * @param id el ID del ticket a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene tickets por estado.
     * 
     * @param estado el estado del ticket
     * @return lista de tickets con el estado especificado
     */
    List<Ticket> obtenerPorEstado(String estado);
    
    /**
     * Obtiene tickets abiertos.
     * 
     * @return lista de tickets abiertos
     */
    List<Ticket> obtenerAbiertos();
    
    /**
     * Obtiene tickets asignados a un Usuarios.
     * 
     * @param usuarioId el ID del Usuarios asignado
     * @return lista de tickets asignados
     */
    List<Ticket> obtenerPorAsignado(Integer usuarioId);
    
    /**
     * Obtiene tickets generados por un Usuarios.
     * 
     * @param usuarioId el ID del Usuarios que genera
     * @return lista de tickets generados
     */
    List<Ticket> obtenerPorCreador(Integer usuarioId);
}
