package com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraResponseDTO {
    private Integer idOrdenCompra;
    private String numeroOc;
    private String proveedor;
    private LocalDate fechaEmision;
    private LocalDate fechaRecepcion;
    private String estado;
    private String bodegaDestinoNombre;
    private List<OrdenCompraDetalleResponseDTO> detalles = new ArrayList<>();

    public Integer getIdOrdenCompra() { return idOrdenCompra; }
    public void setIdOrdenCompra(Integer idOrdenCompra) { this.idOrdenCompra = idOrdenCompra; }
    public String getNumeroOc() { return numeroOc; }
    public void setNumeroOc(String numeroOc) { this.numeroOc = numeroOc; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public LocalDate getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(LocalDate fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getBodegaDestinoNombre() { return bodegaDestinoNombre; }
    public void setBodegaDestinoNombre(String bodegaDestinoNombre) { this.bodegaDestinoNombre = bodegaDestinoNombre; }
    public List<OrdenCompraDetalleResponseDTO> getDetalles() { return detalles; }
    public void setDetalles(List<OrdenCompraDetalleResponseDTO> detalles) { this.detalles = detalles; }
}
