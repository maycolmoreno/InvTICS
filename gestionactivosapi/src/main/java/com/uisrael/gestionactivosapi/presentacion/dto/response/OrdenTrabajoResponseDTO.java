package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrdenTrabajoResponseDTO {

    private Integer idMantenimiento;
    private String sineSnapshoted;
    private LocalDateTime creadoEn;
    private String estadoInterno;
    private String tipoMantenimiento;
    private String prioridad;

    private Integer idEquipo;
    private String serial;
    private String marca;
    private String modelo;
    private String tipoEquipo;
    private String codigoSap;

    private String custodioNombre;
    private String ubicacionNombre;

    private Long diasSinMantenimiento;
    private LocalDate fechaUltimoMantenimiento;

    private List<OrdenActividadResponseDTO> actividades;

    public Integer getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(Integer idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    public String getSineSnapshoted() {
        return sineSnapshoted;
    }

    public void setSineSnapshoted(String sineSnapshoted) {
        this.sineSnapshoted = sineSnapshoted;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getEstadoInterno() {
        return estadoInterno;
    }

    public void setEstadoInterno(String estadoInterno) {
        this.estadoInterno = estadoInterno;
    }

    public String getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(String tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public Integer getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Integer idEquipo) {
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

    public String getCustodioNombre() {
        return custodioNombre;
    }

    public void setCustodioNombre(String custodioNombre) {
        this.custodioNombre = custodioNombre;
    }

    public String getUbicacionNombre() {
        return ubicacionNombre;
    }

    public void setUbicacionNombre(String ubicacionNombre) {
        this.ubicacionNombre = ubicacionNombre;
    }

    public Long getDiasSinMantenimiento() {
        return diasSinMantenimiento;
    }

    public void setDiasSinMantenimiento(Long diasSinMantenimiento) {
        this.diasSinMantenimiento = diasSinMantenimiento;
    }

    public LocalDate getFechaUltimoMantenimiento() {
        return fechaUltimoMantenimiento;
    }

    public void setFechaUltimoMantenimiento(LocalDate fechaUltimoMantenimiento) {
        this.fechaUltimoMantenimiento = fechaUltimoMantenimiento;
    }

    public List<OrdenActividadResponseDTO> getActividades() {
        return actividades;
    }

    public void setActividades(List<OrdenActividadResponseDTO> actividades) {
        this.actividades = actividades;
    }
}
