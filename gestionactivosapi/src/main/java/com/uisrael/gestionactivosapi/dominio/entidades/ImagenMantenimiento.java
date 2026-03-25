package com.uisrael.gestionactivosapi.dominio.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidad que representa una imagen associated with a maintenance record.
 */
public class ImagenMantenimiento implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer idImagen;
    private Integer mantenimientoId;
    private Integer actividadId;
    private String rutaArchivo;
    private String nombreArchivo;
    private String tipoMime;
    private Long tamanioBytes;
    private String descripcion;
    private LocalDateTime fechaSubida;
    private Integer usuarioId;
    
    public ImagenMantenimiento() {
    }
    
    public ImagenMantenimiento(Integer mantenimientoId, String rutaArchivo, String nombreArchivo) {
        this.mantenimientoId = mantenimientoId;
        this.rutaArchivo = rutaArchivo;
        this.nombreArchivo = nombreArchivo;
        this.fechaSubida = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Integer getIdImagen() {
        return idImagen;
    }
    
    public void setIdImagen(Integer idImagen) {
        this.idImagen = idImagen;
    }
    
    public Integer getMantenimientoId() {
        return mantenimientoId;
    }
    
    public void setMantenimientoId(Integer mantenimientoId) {
        this.mantenimientoId = mantenimientoId;
    }
    
    public Integer getActividadId() {
        return actividadId;
    }
    
    public void setActividadId(Integer actividadId) {
        this.actividadId = actividadId;
    }
    
    public String getRutaArchivo() {
        return rutaArchivo;
    }
    
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
    
    public String getNombreArchivo() {
        return nombreArchivo;
    }
    
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
    
    public String getTipoMime() {
        return tipoMime;
    }
    
    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }
    
    public Long getTamanioBytes() {
        return tamanioBytes;
    }
    
    public void setTamanioBytes(Long tamanioBytes) {
        this.tamanioBytes = tamanioBytes;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }
    
    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }
    
    public Integer getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    @Override
    public String toString() {
        return "ImagenMantenimiento [idImagen=" + idImagen + ", mantenimientoId=" + mantenimientoId
            + ", nombreArchivo=" + nombreArchivo + ", tamanioBytes=" + tamanioBytes + "]";
    }
}
