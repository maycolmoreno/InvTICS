package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.util.Map;

import lombok.Data;

@Data
public class EstadisticasEquipoDTO {

    private int totalMantenimientos;
    private Long diasSinMantenimiento;
    private int totalCerrados;
    private int totalEnProceso;
    private Double promedioDiasEntreMantenimientos;
    private Map<Integer, Long> mantsPorAnio;
}
