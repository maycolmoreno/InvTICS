package com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos;

public class RegistrarRecepcionStockCommand {

    private final Integer idOrdenCompra;
    private final Integer idOrdenCompraDetalle;
    private final Integer idBodegaDestino;
    private final Integer cantidad;
    private final String recepcionadoPor;
    private final String observacion;

    public RegistrarRecepcionStockCommand(Integer idOrdenCompra,
                                          Integer idOrdenCompraDetalle,
                                          Integer idBodegaDestino,
                                          Integer cantidad,
                                          String recepcionadoPor,
                                          String observacion) {
        this.idOrdenCompra = idOrdenCompra;
        this.idOrdenCompraDetalle = idOrdenCompraDetalle;
        this.idBodegaDestino = idBodegaDestino;
        this.cantidad = cantidad;
        this.recepcionadoPor = recepcionadoPor;
        this.observacion = observacion;
    }

    public Integer getIdOrdenCompra() { return idOrdenCompra; }
    public Integer getIdOrdenCompraDetalle() { return idOrdenCompraDetalle; }
    public Integer getIdBodegaDestino() { return idBodegaDestino; }
    public Integer getCantidad() { return cantidad; }
    public String getRecepcionadoPor() { return recepcionadoPor; }
    public String getObservacion() { return observacion; }
}
