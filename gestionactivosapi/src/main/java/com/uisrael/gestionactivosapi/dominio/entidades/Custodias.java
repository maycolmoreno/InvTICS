package com.uisrael.gestionactivosapi.dominio.entidades;

import java.io.Serializable;
import java.time.LocalDate;

public class Custodias implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int idCustodiaEquipo;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final String observacion;
    private final boolean estado;

    private Equipos fkEquipo;
    private Custodios fkCustodio;
    private Ubicaciones fkUbicacion;
    private String tipoMovimiento;

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
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public String getObservacion() { return observacion; }
    public boolean isEstado() { return estado; }

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
