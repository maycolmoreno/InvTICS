package com.uisrael.gestionactivosapi.presentacion.dto.request.inventario;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public class DevolucionActivoRequestDTO {

    @NotNull
    private Integer equipoId;

    @NotNull
    private Integer bodegaId;

    private LocalDate fechaDevolucion;
    private String estadoInventarioDestino;
    private String observacion;
    private String estadoFisicoRetorno;
    private String motivo;
    private String realizadoPor;

    public Integer getEquipoId() { return equipoId; }
    public void setEquipoId(Integer equipoId) { this.equipoId = equipoId; }
    public Integer getBodegaId() { return bodegaId; }
    public void setBodegaId(Integer bodegaId) { this.bodegaId = bodegaId; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    public String getEstadoInventarioDestino() { return estadoInventarioDestino; }
    public void setEstadoInventarioDestino(String estadoInventarioDestino) { this.estadoInventarioDestino = estadoInventarioDestino; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public String getEstadoFisicoRetorno() { return estadoFisicoRetorno; }
    public void setEstadoFisicoRetorno(String estadoFisicoRetorno) { this.estadoFisicoRetorno = estadoFisicoRetorno; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getRealizadoPor() { return realizadoPor; }
    public void setRealizadoPor(String realizadoPor) { this.realizadoPor = realizadoPor; }
}
