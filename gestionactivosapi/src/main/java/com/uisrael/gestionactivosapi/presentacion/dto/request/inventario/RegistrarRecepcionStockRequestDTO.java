package com.uisrael.gestionactivosapi.presentacion.dto.request.inventario;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegistrarRecepcionStockRequestDTO {

    @NotNull
    private Integer idBodegaDestino;

    @NotNull
    @Min(1)
    private Integer cantidad;

    @NotBlank
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
