package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class HistorialCompletoDTO {

    private HistorialEquipoDTO equipo;
    private String estadoMantenimiento;
    private List<MantenimientoHistorialDTO> mantenimientos;
    private EstadisticasEquipoDTO estadisticas;
}
