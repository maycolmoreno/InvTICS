package com.uisrael.gestionactivosapi.dominio.dto;

import java.util.Map;

public class EstadisticasEquipoDTO {

    private int totalMantenimientos;
    private Long diasSinMantenimiento;
    private int totalCerrados;
    private int totalEnProceso;
    private Double promedioDiasEntreMantenimientos;
    private Map<Integer, Long> mantsPorAnio;

    public int getTotalMantenimientos() {
        return totalMantenimientos;
    }

    public void setTotalMantenimientos(int totalMantenimientos) {
        this.totalMantenimientos = totalMantenimientos;
    }

    public Long getDiasSinMantenimiento() {
        return diasSinMantenimiento;
    }

    public void setDiasSinMantenimiento(Long diasSinMantenimiento) {
        this.diasSinMantenimiento = diasSinMantenimiento;
    }

    public int getTotalCerrados() {
        return totalCerrados;
    }

    public void setTotalCerrados(int totalCerrados) {
        this.totalCerrados = totalCerrados;
    }

    public int getTotalEnProceso() {
        return totalEnProceso;
    }

    public void setTotalEnProceso(int totalEnProceso) {
        this.totalEnProceso = totalEnProceso;
    }

    public Double getPromedioDiasEntreMantenimientos() {
        return promedioDiasEntreMantenimientos;
    }

    public void setPromedioDiasEntreMantenimientos(Double promedioDiasEntreMantenimientos) {
        this.promedioDiasEntreMantenimientos = promedioDiasEntreMantenimientos;
    }

    public Map<Integer, Long> getMantsPorAnio() {
        return mantsPorAnio;
    }

    public void setMantsPorAnio(Map<Integer, Long> mantsPorAnio) {
        this.mantsPorAnio = mantsPorAnio;
    }
}
