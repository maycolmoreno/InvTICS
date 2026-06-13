package com.uisrael.gestionactivosapi.presentacion.dto.response.inventario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompra;

public class OrdenCompraResponseDTO {
    private Integer idOrdenCompra;
    private String numeroOc;
    private String proveedor;
    private LocalDate fechaEmision;
    private LocalDate fechaRecepcion;
    private EstadoOrdenCompra estado;
    private String observacion;
    private Integer bodegaDestinoId;
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
    public EstadoOrdenCompra getEstado() { return estado; }
    public void setEstado(EstadoOrdenCompra estado) { this.estado = estado; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public Integer getBodegaDestinoId() { return bodegaDestinoId; }
    public void setBodegaDestinoId(Integer bodegaDestinoId) { this.bodegaDestinoId = bodegaDestinoId; }
    public String getBodegaDestinoNombre() { return bodegaDestinoNombre; }
    public void setBodegaDestinoNombre(String bodegaDestinoNombre) { this.bodegaDestinoNombre = bodegaDestinoNombre; }
    public List<OrdenCompraDetalleResponseDTO> getDetalles() { return detalles; }
    public void setDetalles(List<OrdenCompraDetalleResponseDTO> detalles) { this.detalles = detalles; }
}
