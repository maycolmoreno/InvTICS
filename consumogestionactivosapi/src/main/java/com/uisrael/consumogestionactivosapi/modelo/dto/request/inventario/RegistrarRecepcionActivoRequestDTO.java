package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

public class RegistrarRecepcionActivoRequestDTO {
    private Integer idBodegaDestino;
    private Integer idCategoria;
    private Integer idMarca;
    private String modelo;
    private String serial;
    private String condicionAlRecibir;
    private String recepcionadoPor;
    private String observacion;

    public Integer getIdBodegaDestino() { return idBodegaDestino; }
    public void setIdBodegaDestino(Integer idBodegaDestino) { this.idBodegaDestino = idBodegaDestino; }
    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }
    public Integer getIdMarca() { return idMarca; }
    public void setIdMarca(Integer idMarca) { this.idMarca = idMarca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }
    public String getCondicionAlRecibir() { return condicionAlRecibir; }
    public void setCondicionAlRecibir(String condicionAlRecibir) { this.condicionAlRecibir = condicionAlRecibir; }
    public String getRecepcionadoPor() { return recepcionadoPor; }
    public void setRecepcionadoPor(String recepcionadoPor) { this.recepcionadoPor = recepcionadoPor; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
