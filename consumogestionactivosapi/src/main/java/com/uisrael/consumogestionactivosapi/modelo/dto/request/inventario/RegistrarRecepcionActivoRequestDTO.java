package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class RegistrarRecepcionActivoRequestDTO {
    private Integer idBodegaDestino;
    private Integer idCategoria;
    private Integer idMarca;
    private String modelo;
    private String serial;
    private String condicionAlRecibir;
    private String recepcionadoPor;
    private String observacion;

    private String procesador;
    private Integer memoriaRamGb;
    private Integer capacidadAlmacenamientoGb;
    private Boolean licenciaWindowsActivada;
    private String mac;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaCompra;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaGarantia;

    private BigDecimal precioCompra;
    private Boolean etiquetado;

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
    public String getProcesador() { return procesador; }
    public void setProcesador(String procesador) { this.procesador = procesador; }
    public Integer getMemoriaRamGb() { return memoriaRamGb; }
    public void setMemoriaRamGb(Integer memoriaRamGb) { this.memoriaRamGb = memoriaRamGb; }
    public Integer getCapacidadAlmacenamientoGb() { return capacidadAlmacenamientoGb; }
    public void setCapacidadAlmacenamientoGb(Integer capacidadAlmacenamientoGb) { this.capacidadAlmacenamientoGb = capacidadAlmacenamientoGb; }
    public Boolean getLicenciaWindowsActivada() { return licenciaWindowsActivada; }
    public void setLicenciaWindowsActivada(Boolean licenciaWindowsActivada) { this.licenciaWindowsActivada = licenciaWindowsActivada; }
    public String getMac() { return mac; }
    public void setMac(String mac) { this.mac = mac; }
    public LocalDate getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra = fechaCompra; }
    public LocalDate getFechaGarantia() { return fechaGarantia; }
    public void setFechaGarantia(LocalDate fechaGarantia) { this.fechaGarantia = fechaGarantia; }
    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }
    public Boolean getEtiquetado() { return etiquetado; }
    public void setEtiquetado(Boolean etiquetado) { this.etiquetado = etiquetado; }
}
