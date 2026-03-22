package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificacionResponseDTO {
    private Long idNotificacion;
    private Integer usuarioId;
    private String mensaje;
    private String url;
    private Boolean leida;
    private LocalDateTime creadoEn;
    private Integer referenciaMantenimientoId;
}
