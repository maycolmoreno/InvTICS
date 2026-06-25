package com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRecienteDTO {
    private String tipo;
    private String titulo;
    private String detalle;
    private LocalDateTime fecha;
    private String actor;
    private String href;
}
