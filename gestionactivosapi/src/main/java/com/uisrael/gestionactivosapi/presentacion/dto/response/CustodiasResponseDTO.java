package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.time.LocalDate;

public class CustodiasResponseDTO {

    // NUEVO: Numero Acta (cabecera)
    private int idCustodio;

    // Detalle
    private int idCustodiaEquipo;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String observacion;
    private boolean estado;

    private EquiposResponseDTO fkEquipo;
    private CustodiosResponseDTO fkCustodio;

    public int getIdCustodio() { return idCustodio; }
    public void setIdCustodio(int idCustodio) { this.idCustodio = idCustodio; }

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

    public EquiposResponseDTO getFkEquipo() { return fkEquipo; }
    public void setFkEquipo(EquiposResponseDTO fkEquipo) { this.fkEquipo = fkEquipo; }

    public CustodiosResponseDTO getFkCustodio() { return fkCustodio; }
    public void setFkCustodio(CustodiosResponseDTO fkCustodio) { this.fkCustodio = fkCustodio; }

    private String tipoMovimiento;
    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    private String rutaActaPdf;
    public String getRutaActaPdf() { return rutaActaPdf; }
    public void setRutaActaPdf(String rutaActaPdf) { this.rutaActaPdf = rutaActaPdf; }
}

