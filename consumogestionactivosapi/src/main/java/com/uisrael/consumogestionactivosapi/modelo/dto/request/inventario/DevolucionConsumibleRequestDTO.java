package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

public class DevolucionConsumibleRequestDTO {
    private Integer bodegaId;
    private Integer consumibleId;
    private Integer custodioId;
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
