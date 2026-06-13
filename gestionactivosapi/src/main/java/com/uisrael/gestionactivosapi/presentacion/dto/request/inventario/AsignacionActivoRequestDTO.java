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

    public Integer getEquipoId() { return equipoId; }
    public void setEquipoId(Integer equipoId) { this.equipoId = equipoId; }
    public Integer getCustodioId() { return custodioId; }
    public void setCustodioId(Integer custodioId) { this.custodioId = custodioId; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
