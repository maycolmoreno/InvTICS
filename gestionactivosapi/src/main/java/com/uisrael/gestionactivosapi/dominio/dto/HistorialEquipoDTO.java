package com.uisrael.gestionactivosapi.dominio.dto;

import java.time.LocalDate;

public class HistorialEquipoDTO {

    private Integer idEquipo;
    private String marca;
    private String modelo;
    private String serial;
    private String tipoEquipo;
    private String codigoSap;
    private LocalDate fechaCompra;
    private String estadoEquipo;
    private String sistemaOperativo;
    private String procesador;
    private Integer memoriaRamGb;
    private Integer capacidadAlmacenamientoGb;
    private Boolean licenciaWindowsActivada;
    private Boolean unionDominio;
    private String categoriaNombre;

    private String custodioNombre;
    private String departamentoNombre;
    private String ubicacionNombre;
    private String ubicacionCiudad;
    private LocalDate fechaInicioCustodio;

    public Integer getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Integer idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getCodigoSap() {
        return codigoSap;
    }

    public void setCodigoSap(String codigoSap) {
        this.codigoSap = codigoSap;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getEstadoEquipo() {
        return estadoEquipo;
    }

    public void setEstadoEquipo(String estadoEquipo) {
        this.estadoEquipo = estadoEquipo;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public String getProcesador() {
        return procesador;
    }

    public void setProcesador(String procesador) {
        this.procesador = procesador;
    }

    public Integer getMemoriaRamGb() {
        return memoriaRamGb;
    }

    public void setMemoriaRamGb(Integer memoriaRamGb) {
        this.memoriaRamGb = memoriaRamGb;
    }

    public Integer getCapacidadAlmacenamientoGb() {
        return capacidadAlmacenamientoGb;
    }

    public void setCapacidadAlmacenamientoGb(Integer capacidadAlmacenamientoGb) {
        this.capacidadAlmacenamientoGb = capacidadAlmacenamientoGb;
    }

    public Boolean getLicenciaWindowsActivada() {
        return licenciaWindowsActivada;
    }

    public void setLicenciaWindowsActivada(Boolean licenciaWindowsActivada) {
        this.licenciaWindowsActivada = licenciaWindowsActivada;
    }

    public Boolean getUnionDominio() {
        return unionDominio;
    }

    public void setUnionDominio(Boolean unionDominio) {
        this.unionDominio = unionDominio;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public String getCustodioNombre() {
        return custodioNombre;
    }

    public void setCustodioNombre(String custodioNombre) {
        this.custodioNombre = custodioNombre;
    }

    public String getDepartamentoNombre() {
        return departamentoNombre;
    }

    public void setDepartamentoNombre(String departamentoNombre) {
        this.departamentoNombre = departamentoNombre;
    }

    public String getUbicacionNombre() {
        return ubicacionNombre;
    }

    public void setUbicacionNombre(String ubicacionNombre) {
        this.ubicacionNombre = ubicacionNombre;
    }

    public String getUbicacionCiudad() {
        return ubicacionCiudad;
    }

    public void setUbicacionCiudad(String ubicacionCiudad) {
        this.ubicacionCiudad = ubicacionCiudad;
    }

    public LocalDate getFechaInicioCustodio() {
        return fechaInicioCustodio;
    }

    public void setFechaInicioCustodio(LocalDate fechaInicioCustodio) {
        this.fechaInicioCustodio = fechaInicioCustodio;
    }
}
