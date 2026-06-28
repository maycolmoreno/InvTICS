package com.uisrael.gestionactivosapi.presentacion.dto.request.inventario;

public class AdoptarInventarioInicialRequestDTO {

    private Integer bodegaId;
    private Integer custodioId;
    private String codigoCresio;
    private String condicionFisica;
    private String observacion;
    private Boolean etiquetado;

    public Integer getBodegaId() { return bodegaId; }
    public void setBodegaId(Integer bodegaId) { this.bodegaId = bodegaId; }
    public Integer getCustodioId() { return custodioId; }
    public void setCustodioId(Integer custodioId) { this.custodioId = custodioId; }
    public String getCodigoCresio() { return codigoCresio; }
    public void setCodigoCresio(String codigoCresio) { this.codigoCresio = codigoCresio; }
    public String getCondicionFisica() { return condicionFisica; }
    public void setCondicionFisica(String condicionFisica) { this.condicionFisica = condicionFisica; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public Boolean getEtiquetado() { return etiquetado; }
    public void setEtiquetado(Boolean etiquetado) { this.etiquetado = etiquetado; }
}
