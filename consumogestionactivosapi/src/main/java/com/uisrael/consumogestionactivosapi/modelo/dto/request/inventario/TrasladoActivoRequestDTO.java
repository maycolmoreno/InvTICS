package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

public class TrasladoActivoRequestDTO {
    private Integer equipoId;
    private Integer bodegaDestinoId;
    private String observacion;

    public Integer getEquipoId() { return equipoId; }
    public void setEquipoId(Integer equipoId) { this.equipoId = equipoId; }
    public Integer getBodegaDestinoId() { return bodegaDestinoId; }
    public void setBodegaDestinoId(Integer bodegaDestinoId) { this.bodegaDestinoId = bodegaDestinoId; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
