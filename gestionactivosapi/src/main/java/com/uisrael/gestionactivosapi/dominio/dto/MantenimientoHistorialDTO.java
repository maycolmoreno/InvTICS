package com.uisrael.gestionactivosapi.dominio.dto;

import java.time.LocalDateTime;

public class MantenimientoHistorialDTO {

    private Integer idMantenimiento;
    private String sineSnapshoted;
    private String estadoInterno;
    private String descripcion;
    private LocalDateTime fechaCierre;
    private String tecnicoNombre;
    private String tipoInferido;

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

    public String getEstadoInterno() {
        return estadoInterno;
    }

    public void setEstadoInterno(String estadoInterno) {
        this.estadoInterno = estadoInterno;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getTecnicoNombre() {
        return tecnicoNombre;
    }

    public void setTecnicoNombre(String tecnicoNombre) {
        this.tecnicoNombre = tecnicoNombre;
    }

    public String getTipoInferido() {
        return tipoInferido;
    }

    public void setTipoInferido(String tipoInferido) {
        this.tipoInferido = tipoInferido;
    }
}
