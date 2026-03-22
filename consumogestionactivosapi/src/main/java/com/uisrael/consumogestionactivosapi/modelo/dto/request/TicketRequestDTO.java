package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import lombok.Data;

@Data
public class TicketRequestDTO {
    private String titulo;
    private String descripcion;
    private String odooTicketId;
    private String prioridad;
    private String estado;
    private String tipoOrigen;
    private Integer idSolicitante;
    private Integer idEquipo;
    private Integer idTecnicoAsignado;
}
