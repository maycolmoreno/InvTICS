package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ITicketsUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.EstadoTicket;
import com.uisrael.gestionactivosapi.dominio.entidades.PrioridadTicket;
import com.uisrael.gestionactivosapi.dominio.entidades.Ticket;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoOrigenTicket;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CrearTicketRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.TicketResponseDTO;

@RestController
@RequestMapping("/api/tickets")
public class TicketsControlador {

    private final ITicketsUseCase ticketsUseCase;

    public TicketsControlador(ITicketsUseCase ticketsUseCase) {
        this.ticketsUseCase = ticketsUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponseDTO crear(@RequestBody CrearTicketRequestDTO request) {
        Ticket ticket = new Ticket(
                null,
                request.titulo(),
                request.descripcion(),
                request.odooTicketId(),
                request.prioridad() == null ? PrioridadTicket.MEDIA : PrioridadTicket.valueOf(request.prioridad().trim().toUpperCase()),
                request.estado() == null ? EstadoTicket.ABIERTO : EstadoTicket.valueOf(request.estado().trim().toUpperCase()),
                request.tipoOrigen() == null ? TipoOrigenTicket.MANUAL : TipoOrigenTicket.valueOf(request.tipoOrigen().trim().toUpperCase()),
                request.idSolicitante(),
                request.idEquipo(),
                request.idTecnicoAsignado(),
                null,
                null,
                null);
        return toResponse(ticketsUseCase.crear(ticket));
    }

    @PutMapping("/{idTicket}/asignar/{idTecnico}")
    public TicketResponseDTO asignar(@PathVariable Integer idTicket, @PathVariable Integer idTecnico) {
        return toResponse(ticketsUseCase.asignar(idTicket, idTecnico));
    }

    @PostMapping("/{idTicket}/cerrar-y-crear-mantenimiento")
    public TicketResponseDTO cerrarYCrearMantenimiento(@PathVariable Integer idTicket) {
        return toResponse(ticketsUseCase.cerrarYCrearMantenimiento(idTicket));
    }

    @GetMapping("/{idTicket}")
    public TicketResponseDTO obtenerPorId(@PathVariable Integer idTicket) {
        return toResponse(ticketsUseCase.obtenerPorId(idTicket));
    }

    @GetMapping
    public List<TicketResponseDTO> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer idEquipo,
            @RequestParam(required = false) String odooTicketId) {
        EstadoTicket estadoFiltro = estado == null || estado.isBlank()
                ? null
                : EstadoTicket.valueOf(estado.trim().toUpperCase());
        return ticketsUseCase.listar(estadoFiltro, idEquipo, odooTicketId).stream()
                .map(this::toResponse)
                .toList();
    }

    private TicketResponseDTO toResponse(Ticket ticket) {
        return new TicketResponseDTO(
                ticket.idTicket(),
                ticket.titulo(),
                ticket.descripcion(),
                ticket.odooTicketId(),
                ticket.prioridad().name(),
                ticket.estado().name(),
                ticket.tipoOrigen().name(),
                ticket.idSolicitante(),
                ticket.idEquipo(),
                ticket.idTecnicoAsignado(),
                ticket.creadoEn(),
                ticket.actualizadoEn(),
                ticket.fkMantenimiento());
    }
}
