package com.uisrael.gestionactivosapi.presentacion.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActividadManualResponseDTO {
    private Integer idActividad;
    private String nombreActividad;
    private String categoriaActividad;
    private Boolean realizada;
}
