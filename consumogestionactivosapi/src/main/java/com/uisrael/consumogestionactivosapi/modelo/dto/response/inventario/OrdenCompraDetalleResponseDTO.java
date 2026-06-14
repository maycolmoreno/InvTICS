package com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario;

public class OrdenCompraDetalleResponseDTO {
    private Integer idOrdenCompraDetalle;
    private String tipoItem;
    private String estado;
    private String descripcion;
    private Integer cantidadSolicitada;
    private Integer cantidadRecibida;
    private String categoriaNombre;
    private String marcaNombre;
    private String consumibleNombre;

    public Integer getIdOrdenCompraDetalle() { return idOrdenCompraDetalle; }
    public void setIdOrdenCompraDetalle(Integer idOrdenCompraDetalle) { this.idOrdenCompraDetalle = idOrdenCompraDetalle; }
    public String getTipoItem() { return tipoItem; }
    public void setTipoItem(String tipoItem) { this.tipoItem = tipoItem; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(Integer cantidadSolicitada) { this.cantidadSolicitada = cantidadSolicitada; }
    public Integer getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(Integer cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }
    public String getMarcaNombre() { return marcaNombre; }
    public void setMarcaNombre(String marcaNombre) { this.marcaNombre = marcaNombre; }
    public String getConsumibleNombre() { return consumibleNombre; }
    public void setConsumibleNombre(String consumibleNombre) { this.consumibleNombre = consumibleNombre; }

    public int getPendiente() {
        int sol = cantidadSolicitada == null ? 0 : cantidadSolicitada;
        int rec = cantidadRecibida == null ? 0 : cantidadRecibida;
        return Math.max(0, sol - rec);
    }

    public boolean isRecibible() {
        return ("PENDIENTE".equals(estado) || "PARCIAL".equals(estado)) && getPendiente() > 0;
    }
}
