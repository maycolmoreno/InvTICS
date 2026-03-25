package com.uisrael.gestionactivosapi.dominio.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidad que representa una notificación en el sistema.
 */
public class Notificacion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer idNotificacion;
    private Integer usuarioId;
    private String tipo;
    private String asunto;
    private String cuerpo;
    private String canal; // EMAIL, SMS, PUSH
    private boolean enviado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEnvio;
    private String estado; // PENDIENTE, ENVIADO, ERROR
    
    public Notificacion() {
    }
    
    public Notificacion(Integer usuarioId, String tipo, String asunto, String cuerpo, String canal) {
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.asunto = asunto;
        this.cuerpo = cuerpo;
        this.canal = canal;
        this.enviado = false;
        this.estado = "PENDIENTE";
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Integer getIdNotificacion() {
        return idNotificacion;
    }
    
    public void setIdNotificacion(Integer idNotificacion) {
        this.idNotificacion = idNotificacion;
    }
    
    public Integer getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getAsunto() {
        return asunto;
    }
    
    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }
    
    public String getCuerpo() {
        return cuerpo;
    }
    
    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }
    
    public String getCanal() {
        return canal;
    }
    
    public void setCanal(String canal) {
        this.canal = canal;
    }
    
    public boolean isEnviado() {
        return enviado;
    }
    
    public void setEnviado(boolean enviado) {
        this.enviado = enviado;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }
    
    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    // ============ DOMAIN METHODS ============
    
    /**
     * Marca la notificación como enviada
     */
    public void marcarComoEnviada() {
        this.enviado = true;
        this.estado = "ENVIADO";
        this.fechaEnvio = LocalDateTime.now();
    }
    
    /**
     * Marca la notificación con error
     */
    public void marcarConError() {
        this.estado = "ERROR";
    }
    
    /**
     * Valida si la notificación se puede enviar
     * 
     * @return true si está lista para enviar
     */
    public boolean estaPendiente() {
        return !enviado && "PENDIENTE".equals(estado);
    }
    
    @Override
    public String toString() {
        return "Notificacion [idNotificacion=" + idNotificacion + ", usuarioId=" + usuarioId 
            + ", tipo=" + tipo + ", canal=" + canal + ", estado=" + estado + "]";
    }
}
