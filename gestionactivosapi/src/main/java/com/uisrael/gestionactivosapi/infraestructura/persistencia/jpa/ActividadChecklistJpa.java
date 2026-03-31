package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "actividades_checklist")
public class ActividadChecklistJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Integer idActividad;

    @Column(name = "nombre", length = 200, nullable = false)
    private String nombre;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "estado")
    private Boolean estado;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "checklist_categoria",
        joinColumns = @JoinColumn(name = "id_actividad"),
        inverseJoinColumns = @JoinColumn(name = "id_categoria")
    )
    private Set<CategoriaEquiposJpa> categorias = new HashSet<>();

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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Set<CategoriaEquiposJpa> getCategorias() {
        return categorias;
    }

    public void setCategorias(Set<CategoriaEquiposJpa> categorias) {
        this.categorias = categorias;
    }
}
