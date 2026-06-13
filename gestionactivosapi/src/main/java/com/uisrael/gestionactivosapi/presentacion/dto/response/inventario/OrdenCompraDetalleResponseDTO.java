package com.uisrael.gestionactivosapi.presentacion.dto.response.inventario;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoItemInventario;

public class OrdenCompraDetalleResponseDTO {
    private Integer idOrdenCompraDetalle;
    private TipoItemInventario tipoItem;
    private String descripcion;
    private Integer cantidadSolicitada;
    private Integer cantidadRecibida;
    private Integer categoriaId;
    private String categoriaNombre;
    private Integer marcaId;
    private String marcaNombre;
    private Integer consumibleId;
    private String consumibleNombre;

    public Integer getIdOrdenCompraDetalle() { return idOrdenCompraDetalle; }
    public void setIdOrdenCompraDetalle(Integer idOrdenCompraDetalle) { this.idOrdenCompraDetalle = idOrdenCompraDetalle; }
    public TipoItemInventario getTipoItem() { return tipoItem; }
    public void setTipoItem(TipoItemInventario tipoItem) { this.tipoItem = tipoItem; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(Integer cantidadSolicitada) { this.cantidadSolicitada = cantidadSolicitada; }
    public Integer getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(Integer cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }
    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }
    public Integer getMarcaId() { return marcaId; }
    public void setMarcaId(Integer marcaId) { this.marcaId = marcaId; }
    public String getMarcaNombre() { return marcaNombre; }
    public void setMarcaNombre(String marcaNombre) { this.marcaNombre = marcaNombre; }
    public Integer getConsumibleId() { return consumibleId; }
    public void setConsumibleId(Integer consumibleId) { this.consumibleId = consumibleId; }
    public String getConsumibleNombre() { return consumibleNombre; }
    public void setConsumibleNombre(String consumibleNombre) { this.consumibleNombre = consumibleNombre; }
}
