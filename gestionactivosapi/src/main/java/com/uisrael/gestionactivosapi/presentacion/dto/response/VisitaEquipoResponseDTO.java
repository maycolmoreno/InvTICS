package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.time.LocalDate;

public class VisitaEquipoResponseDTO {

    private int idEquipo;
    private String serial;
    private String marca;
    private String modelo;
    private String codigoSap;
    private String custodioNombre;
    private String custodioArea;
    private String ubicacionNombre;
    private LocalDate fechaUltimoMantenimiento;
    private Long diasSinMantenimiento;
    private String estadoMantenimiento;

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
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

    public String getCodigoSap() {
        return codigoSap;
    }

    public void setCodigoSap(String codigoSap) {
        this.codigoSap = codigoSap;
    }

    public String getCustodioNombre() {
        return custodioNombre;
    }

    public void setCustodioNombre(String custodioNombre) {
        this.custodioNombre = custodioNombre;
    }

    public String getCustodioArea() {
        return custodioArea;
    }

    public void setCustodioArea(String custodioArea) {
        this.custodioArea = custodioArea;
    }

    public String getUbicacionNombre() {
        return ubicacionNombre;
    }

    public void setUbicacionNombre(String ubicacionNombre) {
        this.ubicacionNombre = ubicacionNombre;
    }

    public LocalDate getFechaUltimoMantenimiento() {
        return fechaUltimoMantenimiento;
    }

    public void setFechaUltimoMantenimiento(LocalDate fechaUltimoMantenimiento) {
        this.fechaUltimoMantenimiento = fechaUltimoMantenimiento;
    }

    public Long getDiasSinMantenimiento() {
        return diasSinMantenimiento;
    }

    public void setDiasSinMantenimiento(Long diasSinMantenimiento) {
        this.diasSinMantenimiento = diasSinMantenimiento;
    }

    public String getEstadoMantenimiento() {
        return estadoMantenimiento;
    }

    public void setEstadoMantenimiento(String estadoMantenimiento) {
        this.estadoMantenimiento = estadoMantenimiento;
    }
}
