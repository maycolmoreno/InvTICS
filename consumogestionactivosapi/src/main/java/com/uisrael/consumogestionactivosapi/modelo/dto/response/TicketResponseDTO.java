package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TicketResponseDTO {
    private Integer idTicket;
    private String titulo;
    private String descripcion;
    private String odooTicketId;
    private String prioridad;
    private String estado;
    private String tipoOrigen;
    private Integer idSolicitante;
    private Integer idEquipo;
    private Integer idTecnicoAsignado;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private Integer fkMantenimiento;
}
