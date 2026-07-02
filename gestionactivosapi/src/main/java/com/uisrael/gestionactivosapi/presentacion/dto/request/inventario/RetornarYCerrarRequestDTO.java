package com.uisrael.gestionactivosapi.presentacion.dto.request.inventario;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Fase C3: request compuesto para "retornar de reparacion + cerrar OT" en una
 * sola transaccion. El resultadoTecnico es obligatorio (es lo que distingue este
 * flujo del retorno simple). El orquestador retorna el activo y cierra la OT
 * EN_PROCESO del equipo atomicamente.
 */
public class RetornarYCerrarRequestDTO {

    @NotNull
    private Integer equipoId;

    @NotNull
    private Integer bodegaDestinoId;

    private String condicion;
    private LocalDate fechaRetorno;
    private String observacion;

    @NotBlank
    private String resultadoTecnico;
    private String observacionCierre;

    public Integer getEquipoId() { return equipoId; }
    public void setEquipoId(Integer equipoId) { this.equipoId = equipoId; }
    public Integer getBodegaDestinoId() { return bodegaDestinoId; }
    public void setBodegaDestinoId(Integer bodegaDestinoId) { this.bodegaDestinoId = bodegaDestinoId; }
    public String getCondicion() { return condicion; }
    public void setCondicion(String condicion) { this.condicion = condicion; }
    public LocalDate getFechaRetorno() { return fechaRetorno; }
    public void setFechaRetorno(LocalDate fechaRetorno) { this.fechaRetorno = fechaRetorno; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public String getResultadoTecnico() { return resultadoTecnico; }
    public void setResultadoTecnico(String resultadoTecnico) { this.resultadoTecnico = resultadoTecnico; }
    public String getObservacionCierre() { return observacionCierre; }
    public void setObservacionCierre(String observacionCierre) { this.observacionCierre = observacionCierre; }
}
