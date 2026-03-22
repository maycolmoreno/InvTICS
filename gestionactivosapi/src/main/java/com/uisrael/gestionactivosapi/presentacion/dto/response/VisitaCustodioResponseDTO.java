package com.uisrael.gestionactivosapi.presentacion.dto.response;

public class VisitaCustodioResponseDTO {

    private int idCustodio;
    private String nombre;
    private String area;

    public int getIdCustodio() {
        return idCustodio;
    }

    public void setIdCustodio(int idCustodio) {
        this.idCustodio = idCustodio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
