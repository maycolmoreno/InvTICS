package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "checklist_categoria")
@IdClass(ChecklistCategoriaPk.class)
public class ChecklistCategoriaJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_actividad")
    private Integer idActividad;

    @Id
    @Column(name = "id_categoria")
    private Integer idCategoria;

    public Integer getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(Integer idActividad) {
        this.idActividad = idActividad;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }
}
