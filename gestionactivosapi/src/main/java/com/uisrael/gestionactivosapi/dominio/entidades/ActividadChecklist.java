package com.uisrael.gestionactivosapi.dominio.entidades;

public class ActividadChecklist {

    private Integer idActividad;
    private String nombre;
    private Integer orden;
    private boolean estado;
    private boolean aplicaPreventivo;
    private boolean aplicaCorrectivo;

    public Integer getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(Integer idActividad) {
        this.idActividad = idActividad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public boolean isAplicaPreventivo() {
        return aplicaPreventivo;
    }

    public void setAplicaPreventivo(boolean aplicaPreventivo) {
        this.aplicaPreventivo = aplicaPreventivo;
    }

    public boolean isAplicaCorrectivo() {
        return aplicaCorrectivo;
    }

    public void setAplicaCorrectivo(boolean aplicaCorrectivo) {
        this.aplicaCorrectivo = aplicaCorrectivo;
    }
}
