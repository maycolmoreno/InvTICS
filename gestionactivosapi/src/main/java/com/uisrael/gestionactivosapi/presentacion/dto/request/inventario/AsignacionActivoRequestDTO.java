package com.uisrael.gestionactivosapi.presentacion.dto.request.inventario;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public class AsignacionActivoRequestDTO {

    @NotNull
    private Integer equipoId;

    @NotNull
    private Integer custodioId;

    private LocalDate fechaInicio;
    private String observacion;
    private String condicionEntrega;
    private String realizadoPor;

    public Integer getEquipoId() { return equipoId; }
    public void setEquipoId(Integer equipoId) { this.equipoId = equipoId; }
    public Integer getCustodioId() { return custodioId; }
    public void setCustodioId(Integer custodioId) { this.custodioId = custodioId; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public String getCondicionEntrega() { return condicionEntrega; }
    public void setCondicionEntrega(String condicionEntrega) { this.condicionEntrega = condicionEntrega; }
    public String getRealizadoPor() { return realizadoPor; }
    public void setRealizadoPor(String realizadoPor) { this.realizadoPor = realizadoPor; }
}
