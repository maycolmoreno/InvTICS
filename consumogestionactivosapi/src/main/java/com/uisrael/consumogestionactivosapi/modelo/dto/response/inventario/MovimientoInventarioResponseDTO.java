package com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario;

import java.time.LocalDateTime;

public class MovimientoInventarioResponseDTO {
    private Integer idMovimientoInventario;
    private String tipoMovimiento;
    private LocalDateTime fechaMovimiento;
    private Integer equipoId;
    private String equipoCodigo;
    private Integer consumibleId;
    private String consumibleNombre;
    private Integer cantidad;
    private Integer bodegaOrigenId;
    private String bodegaOrigenNombre;
    private Integer bodegaDestinoId;
    private String bodegaDestinoNombre;
    private Integer custodioId;
    private String custodioNombre;
    private String estadoAnterior;
    private String estadoNuevo;
    private String observacion;

    public Integer getIdMovimientoInventario() { return idMovimientoInventario; }
    public void setIdMovimientoInventario(Integer idMovimientoInventario) { this.idMovimientoInventario = idMovimientoInventario; }
    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(LocalDateTime fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
    public Integer getEquipoId() { return equipoId; }
    public void setEquipoId(Integer equipoId) { this.equipoId = equipoId; }
    public String getEquipoCodigo() { return equipoCodigo; }
    public void setEquipoCodigo(String equipoCodigo) { this.equipoCodigo = equipoCodigo; }
    public Integer getConsumibleId() { return consumibleId; }
    public void setConsumibleId(Integer consumibleId) { this.consumibleId = consumibleId; }
    public String getConsumibleNombre() { return consumibleNombre; }
    public void setConsumibleNombre(String consumibleNombre) { this.consumibleNombre = consumibleNombre; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Integer getBodegaOrigenId() { return bodegaOrigenId; }
    public void setBodegaOrigenId(Integer bodegaOrigenId) { this.bodegaOrigenId = bodegaOrigenId; }
    public String getBodegaOrigenNombre() { return bodegaOrigenNombre; }
    public void setBodegaOrigenNombre(String bodegaOrigenNombre) { this.bodegaOrigenNombre = bodegaOrigenNombre; }
    public Integer getBodegaDestinoId() { return bodegaDestinoId; }
    public void setBodegaDestinoId(Integer bodegaDestinoId) { this.bodegaDestinoId = bodegaDestinoId; }
    public String getBodegaDestinoNombre() { return bodegaDestinoNombre; }
    public void setBodegaDestinoNombre(String bodegaDestinoNombre) { this.bodegaDestinoNombre = bodegaDestinoNombre; }
    public Integer getCustodioId() { return custodioId; }
    public void setCustodioId(Integer custodioId) { this.custodioId = custodioId; }
    public String getCustodioNombre() { return custodioNombre; }
    public void setCustodioNombre(String custodioNombre) { this.custodioNombre = custodioNombre; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
