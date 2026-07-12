package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import lombok.Data;

@Data
public class ActividadChecklistRequestDTO {
    private int idActividad;
    private String nombre;
    private Integer orden;
    private Boolean estado;
}
