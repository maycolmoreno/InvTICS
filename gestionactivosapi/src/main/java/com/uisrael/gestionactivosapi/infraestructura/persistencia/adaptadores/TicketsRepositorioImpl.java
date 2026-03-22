package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.EstadoTicket;
import com.uisrael.gestionactivosapi.dominio.entidades.Ticket;
import com.uisrael.gestionactivosapi.dominio.repositorios.ITicketsRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.TicketsJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ITicketsJpaRepositorio;

public class TicketsRepositorioImpl implements ITicketsRepositorio {

    private final ITicketsJpaRepositorio jpaRepositorio;

    public TicketsRepositorioImpl(ITicketsJpaRepositorio jpaRepositorio) {
        this.jpaRepositorio = jpaRepositorio;
    }

    @Override
    public Ticket guardar(Ticket ticket) {
        return toDomain(jpaRepositorio.save(toEntity(ticket)));
    }

    @Override
    public Optional<Ticket> buscarPorId(Integer idTicket) {
        return jpaRepositorio.findById(idTicket).map(this::toDomain);
    }

    @Override
    public Optional<Ticket> buscarPorOdooTicketId(String odooTicketId) {
        return jpaRepositorio.findByOdooTicketId(odooTicketId).map(this::toDomain);
    }

    @Override
    public List<Ticket> listarTodos() {
        return jpaRepositorio.findAllByOrderByActualizadoEnDescIdTicketDesc().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Ticket> buscarPorEstado(EstadoTicket estado) {
        return jpaRepositorio.findByEstadoOrderByActualizadoEnDescIdTicketDesc(estado).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Ticket> buscarPorEquipo(Integer idEquipo) {
        return jpaRepositorio.findByIdEquipoOrderByActualizadoEnDescIdTicketDesc(idEquipo).stream()
                .map(this::toDomain)
                .toList();
    }

    private Ticket toDomain(TicketsJpa entity) {
        return new Ticket(
                entity.getIdTicket(),
                entity.getTitulo(),
                entity.getDescripcion(),
                entity.getOdooTicketId(),
                entity.getPrioridad(),
                entity.getEstado(),
                entity.getTipoOrigen(),
                entity.getIdSolicitante(),
                entity.getIdEquipo(),
                entity.getIdTecnicoAsignado(),
                entity.getCreadoEn(),
                entity.getActualizadoEn(),
                entity.getFkMantenimiento());
    }

    private TicketsJpa toEntity(Ticket ticket) {
        TicketsJpa entity = new TicketsJpa();
        entity.setIdTicket(ticket.idTicket());
        entity.setTitulo(ticket.titulo());
        entity.setDescripcion(ticket.descripcion());
        entity.setOdooTicketId(ticket.odooTicketId());
        entity.setPrioridad(ticket.prioridad());
        entity.setEstado(ticket.estado());
        entity.setTipoOrigen(ticket.tipoOrigen());
        entity.setIdSolicitante(ticket.idSolicitante());
        entity.setIdEquipo(ticket.idEquipo());
        entity.setIdTecnicoAsignado(ticket.idTecnicoAsignado());
        entity.setCreadoEn(ticket.creadoEn());
        entity.setActualizadoEn(ticket.actualizadoEn());
        entity.setFkMantenimiento(ticket.fkMantenimiento());
        return entity;
    }
}
