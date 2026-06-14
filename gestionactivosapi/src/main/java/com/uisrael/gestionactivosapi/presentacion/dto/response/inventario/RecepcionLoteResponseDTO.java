package com.uisrael.gestionactivosapi.presentacion.dto.response.inventario;

import java.time.LocalDateTime;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoRecepcionLote;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoItemInventario;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RecepcionLoteJpa;

public class RecepcionLoteResponseDTO {

    private Integer idRecepcionLote;
    private String uuid;
    private Integer idOrdenCompra;
    private Integer idOrdenCompraDetalle;
    private String numeroLote;
    private LocalDateTime fechaRecepcion;
    private Integer cantidadRecibida;
    private TipoItemInventario tipoItem;
    private EstadoRecepcionLote estado;
    private String observacion;
    private Integer idBodegaDestino;
    private String nombreBodegaDestino;
    private String recepcionadoPor;
    private LocalDateTime recepcionadoEn;

    public static RecepcionLoteResponseDTO from(RecepcionLoteJpa lote) {
        RecepcionLoteResponseDTO dto = new RecepcionLoteResponseDTO();
        dto.idRecepcionLote = lote.getIdRecepcionLote();
        dto.uuid = lote.getUuid();
        dto.idOrdenCompra = lote.getOrdenCompra() != null ? lote.getOrdenCompra().getIdOrdenCompra() : null;
        dto.idOrdenCompraDetalle = lote.getOrdenCompraDetalle() != null
                ? lote.getOrdenCompraDetalle().getIdOrdenCompraDetalle() : null;
        dto.numeroLote = lote.getNumeroLote();
        dto.fechaRecepcion = lote.getFechaRecepcion();
        dto.cantidadRecibida = lote.getCantidadRecibida();
        dto.tipoItem = lote.getTipoItem();
        dto.estado = lote.getEstado();
        dto.observacion = lote.getObservacion();
        if (lote.getBodegaDestino() != null) {
            dto.idBodegaDestino = lote.getBodegaDestino().getIdBodega();
            dto.nombreBodegaDestino = lote.getBodegaDestino().getNombre();
        }
        dto.recepcionadoPor = lote.getRecepcionadoPor();
        dto.recepcionadoEn = lote.getRecepcionadoEn();
        return dto;
    }

    public Integer getIdRecepcionLote() { return idRecepcionLote; }
    public String getUuid() { return uuid; }
    public Integer getIdOrdenCompra() { return idOrdenCompra; }
    public Integer getIdOrdenCompraDetalle() { return idOrdenCompraDetalle; }
    public String getNumeroLote() { return numeroLote; }
    public LocalDateTime getFechaRecepcion() { return fechaRecepcion; }
    public Integer getCantidadRecibida() { return cantidadRecibida; }
    public TipoItemInventario getTipoItem() { return tipoItem; }
    public EstadoRecepcionLote getEstado() { return estado; }
    public String getObservacion() { return observacion; }
    public Integer getIdBodegaDestino() { return idBodegaDestino; }
    public String getNombreBodegaDestino() { return nombreBodegaDestino; }
    public String getRecepcionadoPor() { return recepcionadoPor; }
    public LocalDateTime getRecepcionadoEn() { return recepcionadoEn; }
}
