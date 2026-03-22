package com.uisrael.gestionactivosapi.presentacion.dto.request;

import java.util.List;

public class OrdenGuardarRequestDTO {

    private String estadoGeneral;
    private String observaciones;
    private String firmaBase64;
    private List<ActividadRealizadaRequestDTO> actividades;

    public String getEstadoGeneral() {
        return estadoGeneral;
    }

    public void setEstadoGeneral(String estadoGeneral) {
        this.estadoGeneral = estadoGeneral;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getFirmaBase64() {
        return firmaBase64;
    }

    public void setFirmaBase64(String firmaBase64) {
        this.firmaBase64 = firmaBase64;
    }

    public List<ActividadRealizadaRequestDTO> getActividades() {
        return actividades;
    }

    public void setActividades(List<ActividadRealizadaRequestDTO> actividades) {
        this.actividades = actividades;
    }
}
