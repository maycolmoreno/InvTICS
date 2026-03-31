package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActividadChecklistResponseDTO {
    private Integer idActividad;
    private String nombre;
    private Integer orden;
    private Boolean estado;
    private List<String> categorias;
}
