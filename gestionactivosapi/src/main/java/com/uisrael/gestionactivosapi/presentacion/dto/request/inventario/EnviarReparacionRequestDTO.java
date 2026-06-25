package com.uisrael.gestionactivosapi.presentacion.dto.request.inventario;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public class EnviarReparacionRequestDTO {

    @NotNull
    private Integer equipoId;

    private String motivo;
    private String proveedorTecnico;
    private LocalDate fechaEnvio;
    private String observacion;

    public Integer getEquipoId() { return equipoId; }
    public void setEquipoId(Integer equipoId) { this.equipoId = equipoId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getProveedorTecnico() { return proveedorTecnico; }
    public void setProveedorTecnico(String proveedorTecnico) { this.proveedorTecnico = proveedorTecnico; }
    public LocalDate getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDate fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
