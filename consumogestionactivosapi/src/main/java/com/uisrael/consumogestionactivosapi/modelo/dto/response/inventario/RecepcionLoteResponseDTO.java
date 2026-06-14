package com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario;

import java.time.LocalDateTime;

public class RecepcionLoteResponseDTO {
    private Integer idRecepcionLote;
    private String uuid;
    private String tipoItem;
    private String estado;
    private Integer cantidadRecibida;
    private LocalDateTime fechaRecepcion;
    private String recepcionadoPor;
    private LocalDateTime recepcionadoEn;
    private String nombreBodegaDestino;
    private String observacion;

    public Integer getIdRecepcionLote() { return idRecepcionLote; }
    public void setIdRecepcionLote(Integer idRecepcionLote) { this.idRecepcionLote = idRecepcionLote; }
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }
    public String getTipoItem() { return tipoItem; }
    public void setTipoItem(String tipoItem) { this.tipoItem = tipoItem; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(Integer cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }
    public LocalDateTime getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(LocalDateTime fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }
    public String getRecepcionadoPor() { return recepcionadoPor; }
    public void setRecepcionadoPor(String recepcionadoPor) { this.recepcionadoPor = recepcionadoPor; }
    public LocalDateTime getRecepcionadoEn() { return recepcionadoEn; }
    public void setRecepcionadoEn(LocalDateTime recepcionadoEn) { this.recepcionadoEn = recepcionadoEn; }
    public String getNombreBodegaDestino() { return nombreBodegaDestino; }
    public void setNombreBodegaDestino(String nombreBodegaDestino) { this.nombreBodegaDestino = nombreBodegaDestino; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
