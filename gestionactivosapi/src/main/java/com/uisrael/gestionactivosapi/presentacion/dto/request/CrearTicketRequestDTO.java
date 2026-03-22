package com.uisrael.gestionactivosapi.presentacion.dto.request;

public record CrearTicketRequestDTO(
        String titulo,
        String descripcion,
        String odooTicketId,
        String prioridad,
        String estado,
        String tipoOrigen,
        Integer idSolicitante,
        Integer idEquipo,
        Integer idTecnicoAsignado) {
}
