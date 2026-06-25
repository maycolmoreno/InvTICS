package com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkQueueResumenDTO {
    private String tipo;
    private String titulo;
    private String descripcion;
    private long cantidad;
    private String prioridad;
    private String href;
    private String accionTexto;
    private String icono;
}
