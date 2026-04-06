package com.uisrael.gestionactivosapi.dominio.entidades;

import java.time.LocalDate;

public class Custodias {

    private int idCustodiaEquipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String observacion;
    private boolean estado;

    private Equipos fkEquipo;
    private Custodios fkCustodio;
    private Ubicaciones fkUbicacion;
    private String tipoMovimiento;

    public Custodias() {}

    public Custodias(int idCustodiaEquipo,
                     LocalDate fechaInicio,
                     LocalDate fechaFin,
                     String observacion,
                     boolean estado,
                     Equipos fkEquipo,
                     Custodios fkCustodio) {

        this.idCustodiaEquipo = idCustodiaEquipo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.observacion = observacion;
        this.estado = estado;
        this.fkEquipo = fkEquipo;
        this.fkCustodio = fkCustodio;
    }

    public int getIdCustodiaEquipo() { return idCustodiaEquipo; }
    public void setIdCustodiaEquipo(int idCustodiaEquipo) { this.idCustodiaEquipo = idCustodiaEquipo; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public Equipos getFkEquipo() { return fkEquipo; }
    public void setFkEquipo(Equipos fkEquipo) { this.fkEquipo = fkEquipo; }

    public Custodios getFkCustodio() { return fkCustodio; }
    public void setFkCustodio(Custodios fkCustodio) { this.fkCustodio = fkCustodio; }

    public Ubicaciones getFkUbicacion() { return fkUbicacion; }
    public void setFkUbicacion(Ubicaciones fkUbicacion) { this.fkUbicacion = fkUbicacion; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    @Override
    public String toString() {
        return "Custodias [idCustodiaEquipo=" + idCustodiaEquipo
                + ", fechaInicio=" + fechaInicio
                + ", fechaFin=" + fechaFin
                + ", observacion=" + observacion
                + ", estado=" + estado
                + ", fkEquipo=" + fkEquipo
                + ", fkCustodio=" + fkCustodio + "]";
    }
}
