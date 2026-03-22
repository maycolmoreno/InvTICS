package com.uisrael.gestionactivosapi.dominio.entidades;

import java.time.LocalDateTime;

public record Ticket(
        Integer idTicket,
        String titulo,
        String descripcion,
        String odooTicketId,
        PrioridadTicket prioridad,
        EstadoTicket estado,
        TipoOrigenTicket tipoOrigen,
        Integer idSolicitante,
        Integer idEquipo,
        Integer idTecnicoAsignado,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn,
        Integer fkMantenimiento) {

    public Ticket {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El titulo del ticket es obligatorio");
        }
        if (prioridad == null) {
            throw new IllegalArgumentException("La prioridad del ticket es obligatoria");
        }
        if (estado == null) {
            throw new IllegalArgumentException("El estado del ticket es obligatorio");
        }
        if (tipoOrigen == null) {
            throw new IllegalArgumentException("El tipo de origen del ticket es obligatorio");
        }
        if (tipoOrigen == TipoOrigenTicket.ODOO_HELPDESK
                && (odooTicketId == null || odooTicketId.isBlank())) {
            throw new IllegalArgumentException("El odooTicketId es obligatorio para tickets de Odoo");
        }
    }

    public Ticket conAsignacion(Integer tecnicoId) {
        return new Ticket(idTicket, titulo, descripcion, odooTicketId, prioridad, EstadoTicket.ASIGNADO,
                tipoOrigen, idSolicitante, idEquipo, tecnicoId, creadoEn, LocalDateTime.now(), fkMantenimiento);
    }

    public Ticket conCierre(Integer mantenimientoId) {
        return new Ticket(idTicket, titulo, descripcion, odooTicketId, prioridad, EstadoTicket.CERRADO,
                tipoOrigen, idSolicitante, idEquipo, idTecnicoAsignado, creadoEn, LocalDateTime.now(), mantenimientoId);
    }
}
