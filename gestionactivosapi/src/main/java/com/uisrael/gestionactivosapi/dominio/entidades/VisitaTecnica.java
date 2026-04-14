package com.uisrael.gestionactivosapi.dominio.entidades;

import java.time.LocalDateTime;

/**
 * Entidad que representa una visita técnica a un equipo.
 */
public class VisitaTecnica {
    
    private Integer idVisita;
    private Integer equipoId;
    private Integer tecnicoId;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaRealizada;
    private String estado; // PENDIENTE, EN_PROCESO, COMPLETADA, CANCELADA
    private String razonVisita;
    private String observaciones;
    private String resultado;
    private String ubicacion;
    
    public VisitaTecnica() {
    }
    
    public VisitaTecnica(Integer equipoId, Integer tecnicoId, LocalDateTime fechaProgramada, String razonVisita) {
        this.equipoId = equipoId;
        this.tecnicoId = tecnicoId;
        this.fechaProgramada = fechaProgramada;
        this.razonVisita = razonVisita;
        this.estado = "PENDIENTE";
    }
    
    // Getters y Setters
    public Integer getIdVisita() {
        return idVisita;
    }
    
    public void setIdVisita(Integer idVisita) {
        this.idVisita = idVisita;
    }
    
    public Integer getEquipoId() {
        return equipoId;
    }
    
    public void setEquipoId(Integer equipoId) {
        this.equipoId = equipoId;
    }
    
    public Integer getTecnicoId() {
        return tecnicoId;
    }
    
    public void setTecnicoId(Integer tecnicoId) {
        this.tecnicoId = tecnicoId;
    }
    
    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }
    
    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }
    
    public LocalDateTime getFechaRealizada() {
        return fechaRealizada;
    }
    
    public void setFechaRealizada(LocalDateTime fechaRealizada) {
        this.fechaRealizada = fechaRealizada;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getRazonVisita() {
        return razonVisita;
    }
    
    public void setRazonVisita(String razonVisita) {
        this.razonVisita = razonVisita;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public String getResultado() {
        return resultado;
    }
    
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    
    public String getUbicacion() {
        return ubicacion;
    }
    
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    
    // ============ DOMAIN METHODS ============
    
    /**
     * Verifica si la visita está pendiente
     */
    public boolean estaPendiente() {
        return "PENDIENTE".equals(estado);
    }
    
    /**
     * Verifica si la visita está próxima a ejecutarse
     */
    public boolean estaProxima() {
        if (fechaProgramada == null || !estaPendiente()) {
            return false;
        }
        long minutosRestantes = java.time.temporal.ChronoUnit.MINUTES.between(
            LocalDateTime.now(), 
            fechaProgramada
        );
        return minutosRestantes >= 0 && minutosRestantes <= 1440; // Próxima en 24 horas
    }
    
    /**
     * Marca la visita como completada
     */
    public void marcarCompletada() {
        this.estado = "COMPLETADA";
        this.fechaRealizada = LocalDateTime.now();
    }
    
    /**
     * Cancela la visita
     */
    public void cancelar(String razonCancelacion) {
        this.estado = "CANCELADA";
        this.observaciones = razonCancelacion;
    }
    
    /**
     * Obtiene una descripción de la visita
     */
    public String getDescripcion() {
        return String.format("Visita %s - Equipo %d - %s", estado, equipoId, razonVisita);
    }
    
    @Override
    public String toString() {
        return "VisitaTecnica [idVisita=" + idVisita + ", equipoId=" + equipoId 
            + ", tecnicoId=" + tecnicoId + ", estado=" + estado + "]";
    }
}
