package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

public class RegistrarRecepcionStockRequestDTO {
    private Integer idBodegaDestino;
    private Integer cantidad;
    private String recepcionadoPor;
    private String observacion;

    public Integer getIdBodegaDestino() { return idBodegaDestino; }
    public void setIdBodegaDestino(Integer idBodegaDestino) { this.idBodegaDestino = idBodegaDestino; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getRecepcionadoPor() { return recepcionadoPor; }
    public void setRecepcionadoPor(String recepcionadoPor) { this.recepcionadoPor = recepcionadoPor; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
