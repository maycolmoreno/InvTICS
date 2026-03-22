package com.uisrael.gestionactivosapi.dominio.dto;

import java.util.List;

public class HistorialCompletoDTO {

    private HistorialEquipoDTO equipo;
    private String estadoMantenimiento;
    private List<MantenimientoHistorialDTO> mantenimientos;
    private EstadisticasEquipoDTO estadisticas;

    public HistorialEquipoDTO getEquipo() {
        return equipo;
    }

    public void setEquipo(HistorialEquipoDTO equipo) {
        this.equipo = equipo;
    }

    public String getEstadoMantenimiento() {
        return estadoMantenimiento;
    }

    public void setEstadoMantenimiento(String estadoMantenimiento) {
        this.estadoMantenimiento = estadoMantenimiento;
    }

    public List<MantenimientoHistorialDTO> getMantenimientos() {
        return mantenimientos;
    }

    public void setMantenimientos(List<MantenimientoHistorialDTO> mantenimientos) {
        this.mantenimientos = mantenimientos;
    }

    public EstadisticasEquipoDTO getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(EstadisticasEquipoDTO estadisticas) {
        this.estadisticas = estadisticas;
    }
}
