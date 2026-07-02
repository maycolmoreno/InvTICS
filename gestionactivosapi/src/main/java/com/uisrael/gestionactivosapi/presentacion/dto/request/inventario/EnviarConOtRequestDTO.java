package com.uisrael.gestionactivosapi.presentacion.dto.request.inventario;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

/**
 * Fase C3: request compuesto para "enviar a reparacion + crear OT" en una sola
 * transaccion. Los campos de OT (custodioId, firmaTecnico, detalle) llegan ya
 * resueltos desde el BFF; el orquestador del API ejecuta ambas operaciones
 * atomicamente.
 */
public class EnviarConOtRequestDTO {

    @NotNull
    private Integer equipoId;

    // Datos del cambio de estado (reparacion)
    private String motivo;
    private String proveedorTecnico;
    private LocalDate fechaEnvio;
    private String observacion;

    // Datos de la OT correctiva
    @NotNull
    private Integer custodioId;
    private String firmaTecnico;
    private String detalle;
    private LocalDate proximaFecha;

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
    public Integer getCustodioId() { return custodioId; }
    public void setCustodioId(Integer custodioId) { this.custodioId = custodioId; }
    public String getFirmaTecnico() { return firmaTecnico; }
    public void setFirmaTecnico(String firmaTecnico) { this.firmaTecnico = firmaTecnico; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public LocalDate getProximaFecha() { return proximaFecha; }
    public void setProximaFecha(LocalDate proximaFecha) { this.proximaFecha = proximaFecha; }
}
