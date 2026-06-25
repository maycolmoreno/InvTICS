package com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario;

public class ActivoInventarioResponseDTO {
    private Integer idEquipo;
    private String codigoCresio;
    private String codigoSap;
    private String modelo;
    private String serial;
    private String estadoInventario;
    private Boolean etiquetado;
    private Integer bodegaId;
    private String bodegaNombre;
    private Integer ordenCompraId;
    private String numeroOc;
    private Integer bodegaDestinoId;
    private String bodegaDestinoNombre;
    private String despachadorNombre;
    private String fechaSalida;

    public Integer getIdEquipo() { return idEquipo; }
    public void setIdEquipo(Integer idEquipo) { this.idEquipo = idEquipo; }
    public String getCodigoCresio() { return codigoCresio; }
    public void setCodigoCresio(String codigoCresio) { this.codigoCresio = codigoCresio; }
    public String getCodigoSap() { return codigoSap; }
    public void setCodigoSap(String codigoSap) { this.codigoSap = codigoSap; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }
    public String getEstadoInventario() { return estadoInventario; }
    public void setEstadoInventario(String estadoInventario) { this.estadoInventario = estadoInventario; }
    public Boolean getEtiquetado() { return etiquetado; }
    public void setEtiquetado(Boolean etiquetado) { this.etiquetado = etiquetado; }
    public Integer getBodegaId() { return bodegaId; }
    public void setBodegaId(Integer bodegaId) { this.bodegaId = bodegaId; }
    public String getBodegaNombre() { return bodegaNombre; }
    public void setBodegaNombre(String bodegaNombre) { this.bodegaNombre = bodegaNombre; }
    public Integer getOrdenCompraId() { return ordenCompraId; }
    public void setOrdenCompraId(Integer ordenCompraId) { this.ordenCompraId = ordenCompraId; }
    public String getNumeroOc() { return numeroOc; }
    public void setNumeroOc(String numeroOc) { this.numeroOc = numeroOc; }
    public Integer getBodegaDestinoId() { return bodegaDestinoId; }
    public void setBodegaDestinoId(Integer bodegaDestinoId) { this.bodegaDestinoId = bodegaDestinoId; }
    public String getBodegaDestinoNombre() { return bodegaDestinoNombre; }
    public void setBodegaDestinoNombre(String bodegaDestinoNombre) { this.bodegaDestinoNombre = bodegaDestinoNombre; }
    public String getDespachadorNombre() { return despachadorNombre; }
    public void setDespachadorNombre(String despachadorNombre) { this.despachadorNombre = despachadorNombre; }
    public String getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(String fechaSalida) { this.fechaSalida = fechaSalida; }
}
