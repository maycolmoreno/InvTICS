package com.uisrael.gestionactivosapi.presentacion.dto.request;

public class ActividadRealizadaRequestDTO {

    private Integer idActividad;
    private Boolean realizada;

    public Integer getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(Integer idActividad) {
        this.idActividad = idActividad;
    }

    public Boolean getRealizada() {
        return realizada;
    }

    public void setRealizada(Boolean realizada) {
        this.realizada = realizada;
    }
}
