package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import lombok.Data;

@Data
public class CambiarEstadoActividadRequestDTO {
    private String estado;
    private Integer tiempoRealMinutos;
    private String observaciones;
}
