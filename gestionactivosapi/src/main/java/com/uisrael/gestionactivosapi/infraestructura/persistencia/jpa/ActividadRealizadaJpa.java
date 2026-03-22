package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "actividades_realizadas")
public class ActividadRealizadaJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad_realizada")
    private Integer idActividadRealizada;

    @Column(name = "id_mantenimiento", nullable = false)
    private Integer idMantenimiento;

    @Column(name = "id_actividad", nullable = false)
    private Integer idActividad;

    @Column(name = "realizada")
    private Boolean realizada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mantenimiento", insertable = false, updatable = false)
    private MantenimientosJpa fkMantenimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actividad", insertable = false, updatable = false)
    private ActividadChecklistJpa fkActividad;

    public Integer getIdActividadRealizada() {
        return idActividadRealizada;
    }

    public void setIdActividadRealizada(Integer idActividadRealizada) {
        this.idActividadRealizada = idActividadRealizada;
    }

    public Integer getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(Integer idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

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

    public MantenimientosJpa getFkMantenimiento() {
        return fkMantenimiento;
    }

    public void setFkMantenimiento(MantenimientosJpa fkMantenimiento) {
        this.fkMantenimiento = fkMantenimiento;
    }

    public ActividadChecklistJpa getFkActividad() {
        return fkActividad;
    }

    public void setFkActividad(ActividadChecklistJpa fkActividad) {
        this.fkActividad = fkActividad;
    }
}
