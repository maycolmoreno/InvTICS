package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class ActividadManualRequestDTO {
    private Integer idActividad;
    private String nombreActividad;
    private String categoriaActividad;
    private Boolean realizada;
    @JsonIgnore
    private Boolean aplicaPreventivo;
    @JsonIgnore
    private Boolean aplicaCorrectivo;
}
