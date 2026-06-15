package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraRequestDTO {
    private String numeroOc;
    private String proveedor;
    private LocalDate fechaEmision;
    private String observacion;
    private Integer bodegaDestinoId;
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
