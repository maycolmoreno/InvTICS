package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class NotificacionResponseDTO {
    private Long idNotificacion;
    private Integer usuarioId;
    private String mensaje;
    private String url;
    private Boolean leida;
    private LocalDateTime creadoEn;
    private Integer referenciaMantenimientoId;
}
