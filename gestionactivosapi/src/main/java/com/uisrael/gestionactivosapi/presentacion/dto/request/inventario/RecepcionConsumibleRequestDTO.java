package com.uisrael.gestionactivosapi.presentacion.dto.request.inventario;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RecepcionConsumibleRequestDTO {
    @NotNull
    private Integer ordenCompraId;
    @NotNull
    private Integer bodegaId;
    @NotNull
    private Integer consumibleId;
    @NotNull
    @Min(1)
    private Integer cantidad;
    private String observacion;

    public Integer getOrdenCompraId() { return ordenCompraId; }
    public void setOrdenCompraId(Integer ordenCompraId) { this.ordenCompraId = ordenCompraId; }
    public Integer getBodegaId() { return bodegaId; }
    public void setBodegaId(Integer bodegaId) { this.bodegaId = bodegaId; }
    public Integer getConsumibleId() { return consumibleId; }
    public void setConsumibleId(Integer consumibleId) { this.consumibleId = consumibleId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
