package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import lombok.Data;

@Data
public class OrdenActividadResponseDTO {

    private Integer idActividad;
    private String nombre;
    private String categoria;
    private Integer orden;
    private boolean realizada;
}
