package com.uisrael.gestionactivosapi.dominio.entidades;

import java.time.LocalDateTime;

public class EquipoVisita {

    private final int idEquipo;
    private final int idCustodio;
    private final String serial;
    private final String marca;
    private final String modelo;
    private final String tipoEquipo;
    private final String codigoSap;
    private final String custodioNombre;
    private final String custodioArea;
    private final String ubicacionNombre;
    private final LocalDateTime fechaUltimoMantenimiento;

    public EquipoVisita(int idEquipo, int idCustodio, String serial, String marca, String modelo, String tipoEquipo, String codigoSap,
            String custodioNombre, String custodioArea, String ubicacionNombre,
            LocalDateTime fechaUltimoMantenimiento) {
        this.idEquipo = idEquipo;
        this.idCustodio = idCustodio;
        this.serial = serial;
        this.marca = marca;
        this.modelo = modelo;
        this.tipoEquipo = tipoEquipo;
        this.codigoSap = codigoSap;
        this.custodioNombre = custodioNombre;
        this.custodioArea = custodioArea;
        this.ubicacionNombre = ubicacionNombre;
        this.fechaUltimoMantenimiento = fechaUltimoMantenimiento;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public int getIdCustodio() {
        return idCustodio;
    }

    public String getSerial() {
        return serial;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public String getCodigoSap() {
        return codigoSap;
    }

    public String getCustodioNombre() {
        return custodioNombre;
    }

    public String getCustodioArea() {
        return custodioArea;
    }

    public String getUbicacionNombre() {
        return ubicacionNombre;
    }

    public LocalDateTime getFechaUltimoMantenimiento() {
        return fechaUltimoMantenimiento;
    }
}
