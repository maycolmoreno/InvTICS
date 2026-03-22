package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.dominio.entidades.EstadoTicket;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.TicketsJpa;

public interface ITicketsJpaRepositorio extends JpaRepository<TicketsJpa, Integer> {

    @Override
    @EntityGraph(attributePaths = {"solicitante", "equipo", "tecnicoAsignado", "mantenimiento"})
    Optional<TicketsJpa> findById(Integer id);

    @EntityGraph(attributePaths = {"solicitante", "equipo", "tecnicoAsignado", "mantenimiento"})
    List<TicketsJpa> findAllByOrderByActualizadoEnDescIdTicketDesc();

    @EntityGraph(attributePaths = {"solicitante", "equipo", "tecnicoAsignado", "mantenimiento"})
    List<TicketsJpa> findByEstadoOrderByActualizadoEnDescIdTicketDesc(EstadoTicket estado);

    @EntityGraph(attributePaths = {"solicitante", "equipo", "tecnicoAsignado", "mantenimiento"})
    List<TicketsJpa> findByIdEquipoOrderByActualizadoEnDescIdTicketDesc(Integer idEquipo);

    @EntityGraph(attributePaths = {"solicitante", "equipo", "tecnicoAsignado", "mantenimiento"})
    Optional<TicketsJpa> findByOdooTicketId(String odooTicketId);
}
