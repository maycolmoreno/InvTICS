package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

import java.time.LocalDate;

public class DevolucionActivoRequestDTO {
    private Integer equipoId;
    private Integer bodegaId;
    private LocalDate fechaDevolucion;
    private String estadoInventarioDestino;
    private String observacion;

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
}
