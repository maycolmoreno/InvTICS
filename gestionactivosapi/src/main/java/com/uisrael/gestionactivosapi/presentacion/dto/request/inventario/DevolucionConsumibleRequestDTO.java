package com.uisrael.gestionactivosapi.presentacion.dto.request.inventario;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DevolucionConsumibleRequestDTO {

    @NotNull
    private Integer bodegaId;

    @NotNull
    private Integer consumibleId;

    @NotNull
    private Integer custodioId;

    @NotNull
    @Min(1)
    private Integer cantidad;

    private String observacion;

    public Integer getBodegaId() { return bodegaId; }
    public void setBodegaId(Integer bodegaId) { this.bodegaId = bodegaId; }
    public Integer getConsumibleId() { return consumibleId; }
    public void setConsumibleId(Integer consumibleId) { this.consumibleId = consumibleId; }
    public Integer getCustodioId() { return custodioId; }
    public void setCustodioId(Integer custodioId) { this.custodioId = custodioId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
