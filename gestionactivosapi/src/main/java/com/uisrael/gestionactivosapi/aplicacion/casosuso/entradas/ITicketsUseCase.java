package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.EstadoTicket;
import com.uisrael.gestionactivosapi.dominio.entidades.Ticket;

public interface ITicketsUseCase {

    Ticket crear(Ticket ticket);

    Ticket asignar(Integer idTicket, Integer idTecnicoAsignado);

    Ticket cerrarYCrearMantenimiento(Integer idTicket);

    Ticket obtenerPorId(Integer idTicket);

    List<Ticket> listar(EstadoTicket estado, Integer idEquipo, String odooTicketId);
}
