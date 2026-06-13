package com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario;

public class StockConsumibleResponseDTO {
    private Integer bodegaId;
    private String bodegaNombre;
    private Integer consumibleId;
    private String consumibleCodigo;
    private String consumibleNombre;
    private Integer cantidad;

    public Integer getBodegaId() { return bodegaId; }
    public void setBodegaId(Integer bodegaId) { this.bodegaId = bodegaId; }
    public String getBodegaNombre() { return bodegaNombre; }
    public void setBodegaNombre(String bodegaNombre) { this.bodegaNombre = bodegaNombre; }
    public Integer getConsumibleId() { return consumibleId; }
    public void setConsumibleId(Integer consumibleId) { this.consumibleId = consumibleId; }
    public String getConsumibleCodigo() { return consumibleCodigo; }
    public void setConsumibleCodigo(String consumibleCodigo) { this.consumibleCodigo = consumibleCodigo; }
    public String getConsumibleNombre() { return consumibleNombre; }
    public void setConsumibleNombre(String consumibleNombre) { this.consumibleNombre = consumibleNombre; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
