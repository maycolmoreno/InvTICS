package com.uisrael.gestionactivosapi.presentacion.dto.request;

import java.util.List;

public class OrdenCrearRequestDTO {

    private List<Integer> equiposIds;
    private String tipo;
    private String prioridad;
    private Integer idUsuarioTecnico;

    public List<Integer> getEquiposIds() {
        return equiposIds;
    }

    public void setEquiposIds(List<Integer> equiposIds) {
        this.equiposIds = equiposIds;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public Integer getIdUsuarioTecnico() {
        return idUsuarioTecnico;
    }

    public void setIdUsuarioTecnico(Integer idUsuarioTecnico) {
        this.idUsuarioTecnico = idUsuarioTecnico;
    }
}
