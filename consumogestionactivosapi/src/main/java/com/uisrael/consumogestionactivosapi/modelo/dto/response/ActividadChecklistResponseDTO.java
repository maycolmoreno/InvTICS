package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import lombok.Data;

@Data
public class ActividadChecklistResponseDTO {
    private Integer idActividad;
    private String nombre;
    private Integer orden;
    private Boolean estado;
}
