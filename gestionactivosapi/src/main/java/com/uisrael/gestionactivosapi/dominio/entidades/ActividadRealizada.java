package com.uisrael.gestionactivosapi.dominio.entidades;

public class ActividadRealizada {

    private Integer idActividadRealizada;
    private Integer idMantenimiento;
    private Integer idActividad;
    private boolean realizada;

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

    public boolean isRealizada() {
        return realizada;
    }

    public void setRealizada(boolean realizada) {
        this.realizada = realizada;
    }
}
