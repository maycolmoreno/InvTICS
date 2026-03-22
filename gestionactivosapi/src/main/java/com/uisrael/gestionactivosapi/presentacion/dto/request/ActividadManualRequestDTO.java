package com.uisrael.gestionactivosapi.presentacion.dto.request;

import lombok.Data;

@Data
public class ActividadManualRequestDTO {
    private Integer idActividad;
    private String nombreActividad;
    private String categoriaActividad;
    private Boolean realizada;
}
