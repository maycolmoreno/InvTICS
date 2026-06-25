package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

public class RegistrarEtiquetaRequestDTO {

    private Boolean etiquetado;
    private String codigoCresio;
    private String observacion;

    public Boolean getEtiquetado() { return etiquetado; }
    public void setEtiquetado(Boolean etiquetado) { this.etiquetado = etiquetado; }
    public String getCodigoCresio() { return codigoCresio; }
    public void setCodigoCresio(String codigoCresio) { this.codigoCresio = codigoCresio; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
