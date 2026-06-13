package com.uisrael.gestionactivosapi.presentacion.dto.request.inventario;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoItemInventario;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrdenCompraDetalleRequestDTO {
    @NotNull
    private TipoItemInventario tipoItem;
    @NotBlank
    private String descripcion;
    @NotNull
    @Min(1)
    private Integer cantidadSolicitada;
    private Integer categoriaId;
    private Integer marcaId;
    private Integer consumibleId;

    public TipoItemInventario getTipoItem() { return tipoItem; }
    public void setTipoItem(TipoItemInventario tipoItem) { this.tipoItem = tipoItem; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(Integer cantidadSolicitada) { this.cantidadSolicitada = cantidadSolicitada; }
    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
    public Integer getMarcaId() { return marcaId; }
    public void setMarcaId(Integer marcaId) { this.marcaId = marcaId; }
    public Integer getConsumibleId() { return consumibleId; }
    public void setConsumibleId(Integer consumibleId) { this.consumibleId = consumibleId; }
}
