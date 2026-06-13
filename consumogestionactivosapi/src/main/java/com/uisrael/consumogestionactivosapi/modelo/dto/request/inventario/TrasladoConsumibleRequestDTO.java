package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

public class TrasladoConsumibleRequestDTO {
    private Integer bodegaOrigenId;
    private Integer bodegaDestinoId;
    private Integer consumibleId;
    private Integer cantidad;
    private String observacion;

    public Integer getBodegaOrigenId() { return bodegaOrigenId; }
    public void setBodegaOrigenId(Integer bodegaOrigenId) { this.bodegaOrigenId = bodegaOrigenId; }
    public Integer getBodegaDestinoId() { return bodegaDestinoId; }
    public void setBodegaDestinoId(Integer bodegaDestinoId) { this.bodegaDestinoId = bodegaDestinoId; }
    public Integer getConsumibleId() { return consumibleId; }
    public void setConsumibleId(Integer consumibleId) { this.consumibleId = consumibleId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
