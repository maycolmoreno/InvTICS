package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

import java.time.LocalDate;

public class AsignacionActivoRequestDTO {
    private Integer equipoId;
    private Integer custodioId;
    private LocalDate fechaInicio;
    private String condicionEntrega;
    private String observacion;
    private String realizadoPor;

    public Integer getEquipoId() { return equipoId; }
    public void setEquipoId(Integer equipoId) { this.equipoId = equipoId; }
    public Integer getCustodioId() { return custodioId; }
    public void setCustodioId(Integer custodioId) { this.custodioId = custodioId; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public String getCondicionEntrega() { return condicionEntrega; }
    public void setCondicionEntrega(String condicionEntrega) { this.condicionEntrega = condicionEntrega; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public String getRealizadoPor() { return realizadoPor; }
    public void setRealizadoPor(String realizadoPor) { this.realizadoPor = realizadoPor; }
}
