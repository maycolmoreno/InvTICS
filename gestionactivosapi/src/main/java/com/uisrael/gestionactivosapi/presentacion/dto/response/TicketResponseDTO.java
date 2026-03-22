package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.time.LocalDateTime;

public record TicketResponseDTO(
        Integer idTicket,
        String titulo,
        String descripcion,
        String odooTicketId,
        String prioridad,
        String estado,
        String tipoOrigen,
        Integer idSolicitante,
        Integer idEquipo,
        Integer idTecnicoAsignado,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn,
        Integer fkMantenimiento) {
}
