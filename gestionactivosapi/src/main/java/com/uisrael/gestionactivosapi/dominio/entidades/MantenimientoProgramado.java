package com.uisrael.gestionactivosapi.dominio.entidades;

import java.time.LocalDateTime;

/**
 * Entidad que representa un mantenimiento programado.
 */
public class MantenimientoProgramado {
    
    private Integer idProgramado;
    private Integer equipoId;
    private String tipo; // preventivo, predictivo, etc
    private LocalDateTime fechaProgramada;
    private Integer frecuenciaDias;
    private LocalDateTime proximaFecha;
    private String descripcion;
    private String estado; // activo, completado, cancelado
    private boolean activo;
    private LocalDateTime fechaCreacion;
    
    public MantenimientoProgramado() {
    }
    
    public MantenimientoProgramado(Integer equipoId, String tipo, LocalDateTime fechaProgramada, Integer frecuenciaDias) {
        this.equipoId = equipoId;
        this.tipo = tipo;
        this.fechaProgramada = fechaProgramada;
        this.frecuenciaDias = frecuenciaDias;
        this.estado = "activo";
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Integer getIdProgramado() {
        return idProgramado;
    }
    
    public void setIdProgramado(Integer idProgramado) {
        this.idProgramado = idProgramado;
    }
    
    public Integer getEquipoId() {
        return equipoId;
    }
    
    public void setEquipoId(Integer equipoId) {
        this.equipoId = equipoId;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }
    
    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada =fechaProgramada;
    }
    
    public Integer getFrecuenciaDias() {
        return frecuenciaDias;
    }
    
    public void setFrecuenciaDias(Integer frecuenciaDias) {
        this.frecuenciaDias = frecuenciaDias;
    }
    
    public LocalDateTime getProximaFecha() {
        return proximaFecha;
    }
    
    public void setProximaFecha(LocalDateTime proximaFecha) {
        this.proximaFecha = proximaFecha;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    @Override
    public String toString() {
        return "MantenimientoProgramado [idProgramado=" + idProgramado + ", equipoId=" + equipoId
            + ", tipo=" + tipo + ", estado=" + estado + "]";
    }
}
