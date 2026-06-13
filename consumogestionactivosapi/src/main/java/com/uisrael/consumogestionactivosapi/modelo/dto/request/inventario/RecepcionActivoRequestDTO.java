package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RecepcionActivoRequestDTO {
    private Integer ordenCompraId;
    private Integer bodegaId;
    private Integer categoriaId;
    private Integer marcaId;
    private String modelo;
    private String serial;
    private String procesador;
    private Integer memoriaRamGb;
    private Integer capacidadAlmacenamientoGb;
    private Boolean licenciaWindowsActivada;
    private String mac;
    private LocalDate fechaCompra;
    private LocalDate fechaGarantia;
    private BigDecimal precioCompra;
    private Boolean etiquetado;
    private String observacion;

    public Integer getOrdenCompraId() { return ordenCompraId; }
    public void setOrdenCompraId(Integer ordenCompraId) { this.ordenCompraId = ordenCompraId; }
    public Integer getBodegaId() { return bodegaId; }
    public void setBodegaId(Integer bodegaId) { this.bodegaId = bodegaId; }
    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
    public Integer getMarcaId() { return marcaId; }
    public void setMarcaId(Integer marcaId) { this.marcaId = marcaId; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }
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
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
