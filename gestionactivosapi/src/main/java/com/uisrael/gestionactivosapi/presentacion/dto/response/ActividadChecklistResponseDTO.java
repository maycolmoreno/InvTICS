package com.uisrael.gestionactivosapi.presentacion.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActividadChecklistResponseDTO {
    private Integer idActividad;
    private String nombre;
    private Integer orden;
    private Boolean estado;
}
