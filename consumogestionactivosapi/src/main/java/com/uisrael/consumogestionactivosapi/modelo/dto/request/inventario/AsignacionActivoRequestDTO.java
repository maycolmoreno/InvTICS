package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

import java.time.LocalDate;

public class AsignacionActivoRequestDTO {
    private Integer equipoId;
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
