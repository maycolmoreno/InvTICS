package com.uisrael.gestionactivosapi.presentacion.dto.request.inventario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrdenCompraRequestDTO {
    @NotBlank
    private String numeroOc;
    private String proveedor;
    private LocalDate fechaEmision;
    private String observacion;
    @NotNull
    private Integer bodegaDestinoId;
    @Valid
    private List<OrdenCompraDetalleRequestDTO> detalles = new ArrayList<>();

    public String getNumeroOc() { return numeroOc; }
    public void setNumeroOc(String numeroOc) { this.numeroOc = numeroOc; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public Integer getBodegaDestinoId() { return bodegaDestinoId; }
    public void setBodegaDestinoId(Integer bodegaDestinoId) { this.bodegaDestinoId = bodegaDestinoId; }
    public List<OrdenCompraDetalleRequestDTO> getDetalles() { return detalles; }
    public void setDetalles(List<OrdenCompraDetalleRequestDTO> detalles) { this.detalles = detalles; }
}
