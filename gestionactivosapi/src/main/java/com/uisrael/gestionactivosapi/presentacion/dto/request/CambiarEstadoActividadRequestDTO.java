package com.uisrael.gestionactivosapi.presentacion.dto.request;

import lombok.Data;

@Data
public class CambiarEstadoActividadRequestDTO {

    private String estado;
    private Integer tiempoRealMinutos;
    private String observaciones;
}
