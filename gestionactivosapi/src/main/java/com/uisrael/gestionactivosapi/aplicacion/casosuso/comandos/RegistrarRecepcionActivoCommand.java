package com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RegistrarRecepcionActivoCommand {

    private final Integer idOrdenCompra;
    private final Integer idOrdenCompraDetalle;
    private final Integer idBodegaDestino;
    private final Integer idCategoria;
    private final Integer idMarca;
    private final String modelo;
    private final String serial;
    private final String condicionAlRecibir;
    private final String recepcionadoPor;
    private final String observacion;
    private final String procesador;
    private final Integer memoriaRamGb;
    private final Integer capacidadAlmacenamientoGb;
    private final Boolean licenciaWindowsActivada;
    private final String mac;
    private final LocalDate fechaCompra;
    private final LocalDate fechaGarantia;
    private final BigDecimal precioCompra;
    private final Boolean etiquetado;

    public RegistrarRecepcionActivoCommand(Integer idOrdenCompra,
                                           Integer idOrdenCompraDetalle,
                                           Integer idBodegaDestino,
                                           Integer idCategoria,
                                           Integer idMarca,
                                           String modelo,
                                           String serial,
                                           String condicionAlRecibir,
                                           String recepcionadoPor,
                                           String observacion,
                                           String procesador,
                                           Integer memoriaRamGb,
                                           Integer capacidadAlmacenamientoGb,
                                           Boolean licenciaWindowsActivada,
                                           String mac,
                                           LocalDate fechaCompra,
                                           LocalDate fechaGarantia,
                                           BigDecimal precioCompra,
                                           Boolean etiquetado) {
        this.idOrdenCompra = idOrdenCompra;
        this.idOrdenCompraDetalle = idOrdenCompraDetalle;
        this.idBodegaDestino = idBodegaDestino;
        this.idCategoria = idCategoria;
        this.idMarca = idMarca;
        this.modelo = modelo;
        this.serial = serial;
        this.condicionAlRecibir = condicionAlRecibir;
        this.recepcionadoPor = recepcionadoPor;
        this.observacion = observacion;
        this.procesador = procesador;
        this.memoriaRamGb = memoriaRamGb;
        this.capacidadAlmacenamientoGb = capacidadAlmacenamientoGb;
        this.licenciaWindowsActivada = licenciaWindowsActivada;
        this.mac = mac;
        this.fechaCompra = fechaCompra;
        this.fechaGarantia = fechaGarantia;
        this.precioCompra = precioCompra;
        this.etiquetado = etiquetado;
    }

    public Integer getIdOrdenCompra() { return idOrdenCompra; }
    public Integer getIdOrdenCompraDetalle() { return idOrdenCompraDetalle; }
    public Integer getIdBodegaDestino() { return idBodegaDestino; }
    public Integer getIdCategoria() { return idCategoria; }
    public Integer getIdMarca() { return idMarca; }
    public String getModelo() { return modelo; }
    public String getSerial() { return serial; }
    public String getCondicionAlRecibir() { return condicionAlRecibir; }
    public String getRecepcionadoPor() { return recepcionadoPor; }
    public String getObservacion() { return observacion; }
    public String getProcesador() { return procesador; }
    public Integer getMemoriaRamGb() { return memoriaRamGb; }
    public Integer getCapacidadAlmacenamientoGb() { return capacidadAlmacenamientoGb; }
    public Boolean getLicenciaWindowsActivada() { return licenciaWindowsActivada; }
    public String getMac() { return mac; }
    public LocalDate getFechaCompra() { return fechaCompra; }
    public LocalDate getFechaGarantia() { return fechaGarantia; }
    public BigDecimal getPrecioCompra() { return precioCompra; }
    public Boolean getEtiquetado() { return etiquetado; }
}
