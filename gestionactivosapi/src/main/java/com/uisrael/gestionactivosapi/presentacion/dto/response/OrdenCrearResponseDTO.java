package com.uisrael.gestionactivosapi.presentacion.dto.response;

public class OrdenCrearResponseDTO {

    private Integer idMantenimiento;

    public OrdenCrearResponseDTO() {
    }

    public OrdenCrearResponseDTO(Integer idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    public Integer getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(Integer idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }
}
