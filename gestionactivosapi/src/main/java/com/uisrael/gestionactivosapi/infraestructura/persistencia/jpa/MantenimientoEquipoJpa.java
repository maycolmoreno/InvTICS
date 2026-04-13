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
@Table(name = "mantenimiento_equipos")
public class MantenimientoEquipoJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "mantenimiento_id", nullable = false)
    private Integer mantenimientoId;

    @Column(name = "equipo_id", nullable = false)
    private Integer equipoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", insertable = false, updatable = false)
    private EquiposJpa equipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mantenimiento_id", insertable = false, updatable = false)
    private MantenimientosJpa mantenimiento;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMantenimientoId() {
        return mantenimientoId;
    }

    public void setMantenimientoId(Integer mantenimientoId) {
        this.mantenimientoId = mantenimientoId;
    }

    public Integer getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(Integer equipoId) {
        this.equipoId = equipoId;
    }

    public EquiposJpa getEquipo() {
        return equipo;
    }

    public void setEquipo(EquiposJpa equipo) {
        this.equipo = equipo;
    }

    public MantenimientosJpa getMantenimiento() {
        return mantenimiento;
    }

    public void setMantenimiento(MantenimientosJpa mantenimiento) {
        this.mantenimiento = mantenimiento;
    }
}
