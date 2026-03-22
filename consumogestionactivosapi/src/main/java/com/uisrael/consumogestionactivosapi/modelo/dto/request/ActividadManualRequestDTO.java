package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import lombok.Data;

@Data
public class ActividadManualRequestDTO {
    private Integer idActividad;
    private String nombreActividad;
    private String categoriaActividad;
    private Boolean realizada;
}
