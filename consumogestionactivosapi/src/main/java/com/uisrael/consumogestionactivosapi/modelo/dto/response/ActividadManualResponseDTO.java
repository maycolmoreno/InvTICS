package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import lombok.Data;

@Data
public class ActividadManualResponseDTO {
    private Integer idActividad;
    private String nombreActividad;
    private String categoriaActividad;
    private Boolean realizada;
}
