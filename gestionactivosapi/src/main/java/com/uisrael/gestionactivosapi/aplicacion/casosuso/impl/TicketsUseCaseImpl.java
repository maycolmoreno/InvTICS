package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ITicketsUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.EstadoInternoMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.EstadoTicket;
import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.dominio.entidades.PrioridadTicket;
import com.uisrael.gestionactivosapi.dominio.entidades.Ticket;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoOrigenMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoOrigenTicket;
import com.uisrael.gestionactivosapi.dominio.repositorios.IMantenimientosRepositorio;
import com.uisrael.gestionactivosapi.dominio.repositorios.ITicketsRepositorio;

public class TicketsUseCaseImpl implements ITicketsUseCase {

    private final ITicketsRepositorio ticketsRepositorio;
    private final IMantenimientosRepositorio mantenimientosRepositorio;

    public TicketsUseCaseImpl(ITicketsRepositorio ticketsRepositorio,
            IMantenimientosRepositorio mantenimientosRepositorio) {
        this.ticketsRepositorio = ticketsRepositorio;
        this.mantenimientosRepositorio = mantenimientosRepositorio;
    }

    @Override
    @Transactional
    public Ticket crear(Ticket ticket) {
        LocalDateTime ahora = LocalDateTime.now();
        Ticket nuevo = new Ticket(
                ticket.idTicket(),
                ticket.titulo(),
                ticket.descripcion(),
                ticket.odooTicketId(),
                ticket.prioridad() == null ? PrioridadTicket.MEDIA : ticket.prioridad(),
                ticket.estado() == null ? EstadoTicket.ABIERTO : ticket.estado(),
                ticket.tipoOrigen(),
                ticket.idSolicitante(),
                ticket.idEquipo(),
                ticket.idTecnicoAsignado(),
                ticket.creadoEn() == null ? ahora : ticket.creadoEn(),
                ahora,
                ticket.fkMantenimiento());
        return ticketsRepositorio.guardar(nuevo);
    }

    @Override
    @Transactional
    public Ticket asignar(Integer idTicket, Integer idTecnicoAsignado) {
        if (idTecnicoAsignado == null) {
            throw new IllegalArgumentException("El tecnico asignado es obligatorio");
        }
        Ticket ticket = obtenerPorId(idTicket);
        return ticketsRepositorio.guardar(ticket.conAsignacion(idTecnicoAsignado));
    }

    @Override
    @Transactional
    public Ticket cerrarYCrearMantenimiento(Integer idTicket) {
        Ticket ticket = obtenerPorId(idTicket);
        if (ticket.idEquipo() == null) {
            throw new IllegalArgumentException("El ticket debe estar vinculado a un equipo para generar mantenimiento");
        }
        if (ticket.idTecnicoAsignado() == null) {
            throw new IllegalArgumentException("El ticket debe tener un tecnico asignado para cerrarse");
        }

        Mantenimientos mantenimiento = new Mantenimientos();
        mantenimiento.setEquipoId(ticket.idEquipo());
        mantenimiento.setIdCliente(ticket.idSolicitante());
        mantenimiento.setIdUsuario(ticket.idTecnicoAsignado());
        mantenimiento.setDescripcion(ticket.descripcion());
        mantenimiento.setFechaProgramada(LocalDateTime.now());
        mantenimiento.setCreadoEn(LocalDateTime.now());
        mantenimiento.setEstado("PENDIENTE");
        mantenimiento.setEstadoInterno(EstadoInternoMantenimiento.PENDIENTE);
        mantenimiento.setTipoOrigen(ticket.tipoOrigen() == TipoOrigenTicket.ODOO_HELPDESK
                ? TipoOrigenMantenimiento.ODOO_HELPDESK
                : TipoOrigenMantenimiento.MANUAL);
        mantenimiento.setOdooTicketId(ticket.odooTicketId());
        mantenimiento.setActivo(Boolean.TRUE);

        Mantenimientos guardado = mantenimientosRepositorio.guardar(mantenimiento);
        return ticketsRepositorio.guardar(ticket.conCierre(guardado.getIdMantenimiento()));
    }

    @Override
    public Ticket obtenerPorId(Integer idTicket) {
        return ticketsRepositorio.buscarPorId(idTicket)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ticket no encontrado"));
    }

    @Override
    public List<Ticket> listar(EstadoTicket estado, Integer idEquipo, String odooTicketId) {
        if (odooTicketId != null && !odooTicketId.isBlank()) {
            return ticketsRepositorio.buscarPorOdooTicketId(odooTicketId).stream().toList();
        }
        if (idEquipo != null) {
            return ticketsRepositorio.buscarPorEquipo(idEquipo);
        }
        if (estado != null) {
            return ticketsRepositorio.buscarPorEstado(estado);
        }
        return ticketsRepositorio.listarTodos();
    }
}
