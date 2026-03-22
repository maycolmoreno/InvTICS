package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.EstadoTicket;
import com.uisrael.gestionactivosapi.dominio.entidades.Ticket;

public interface ITicketsRepositorio {

    Ticket guardar(Ticket ticket);

    Optional<Ticket> buscarPorId(Integer idTicket);

    Optional<Ticket> buscarPorOdooTicketId(String odooTicketId);

    List<Ticket> listarTodos();

    List<Ticket> buscarPorEstado(EstadoTicket estado);

    List<Ticket> buscarPorEquipo(Integer idEquipo);
}
