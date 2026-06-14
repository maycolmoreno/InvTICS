package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.BodegaRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RegistrarRecepcionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RegistrarRecepcionStockRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.RecepcionLoteResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AsignacionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AsignacionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.BajaActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.ConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.DevolucionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.DevolucionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.OrdenCompraRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RecepcionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RecepcionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.MovimientoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.StockConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;

@Service
public class InventarioOperacionServicioImpl implements IInventarioOperacionServicio {

    private final RestClient clienteWeb;

    public InventarioOperacionServicioImpl(RestClient clienteWeb) {
        this.clienteWeb = clienteWeb;
    }

    @Override
    public List<BodegaResponseDTO> listarBodegas() {
        return clienteWeb.get().uri("/inventario/bodegas").retrieve()
                .body(new ParameterizedTypeReference<List<BodegaResponseDTO>>() {});
    }

    @Override
    public BodegaResponseDTO crearBodega(BodegaRequestDTO request) {
        return clienteWeb.post().uri("/inventario/bodegas").body(request).retrieve().body(BodegaResponseDTO.class);
    }

    @Override
    public List<ConsumibleResponseDTO> listarConsumibles() {
        return clienteWeb.get().uri("/inventario/consumibles").retrieve()
                .body(new ParameterizedTypeReference<List<ConsumibleResponseDTO>>() {});
    }

    @Override
    public ConsumibleResponseDTO crearConsumible(ConsumibleRequestDTO request) {
        return clienteWeb.post().uri("/inventario/consumibles").body(request).retrieve().body(ConsumibleResponseDTO.class);
    }

    @Override
    public List<OrdenCompraResponseDTO> listarOrdenesCompra() {
        return clienteWeb.get().uri("/inventario/ordenes-compra").retrieve()
                .body(new ParameterizedTypeReference<List<OrdenCompraResponseDTO>>() {});
    }

    @Override
    public OrdenCompraResponseDTO obtenerOrdenCompra(Integer id) {
        return clienteWeb.get().uri("/ordenes-compra/{id}", id).retrieve().body(OrdenCompraResponseDTO.class);
    }

    @Override
    public OrdenCompraResponseDTO crearOrdenCompra(OrdenCompraRequestDTO request) {
        return clienteWeb.post().uri("/inventario/ordenes-compra").body(request).retrieve().body(OrdenCompraResponseDTO.class);
    }

    @Override
    public OrdenCompraResponseDTO confirmarRecepcionOrden(Integer ordenCompraId) {
        return clienteWeb.post().uri("/inventario/ordenes-compra/{id}/confirmar-recepcion", ordenCompraId)
                .retrieve()
                .body(OrdenCompraResponseDTO.class);
    }

    @Override
    public ActivoInventarioResponseDTO recibirActivo(RecepcionActivoRequestDTO request) {
        return clienteWeb.post().uri("/inventario/recepcion/activos").body(request).retrieve()
                .body(ActivoInventarioResponseDTO.class);
    }

    @Override
    public StockConsumibleResponseDTO recibirConsumible(RecepcionConsumibleRequestDTO request) {
        return clienteWeb.post().uri("/inventario/recepcion/consumibles").body(request).retrieve()
                .body(StockConsumibleResponseDTO.class);
    }

    @Override
    public List<StockConsumibleResponseDTO> listarStockPorBodega(Integer bodegaId) {
        return clienteWeb.get().uri("/inventario/bodegas/{id}/stock", bodegaId).retrieve()
                .body(new ParameterizedTypeReference<List<StockConsumibleResponseDTO>>() {});
    }

    @Override
    public List<MovimientoInventarioResponseDTO> listarMovimientosRecientes() {
        return clienteWeb.get().uri("/inventario/movimientos").retrieve()
                .body(new ParameterizedTypeReference<List<MovimientoInventarioResponseDTO>>() {});
    }

    @Override
    public List<ActivoInventarioResponseDTO> listarActivosEnBodega() {
        return clienteWeb.get().uri("/inventario/activos/en-bodega").retrieve()
                .body(new ParameterizedTypeReference<List<ActivoInventarioResponseDTO>>() {});
    }

    @Override
    public ActivoInventarioResponseDTO asignarActivo(AsignacionActivoRequestDTO request) {
        return clienteWeb.post().uri("/inventario/asignaciones/activos").body(request).retrieve()
                .body(ActivoInventarioResponseDTO.class);
    }

    @Override
    public StockConsumibleResponseDTO asignarConsumible(AsignacionConsumibleRequestDTO request) {
        return clienteWeb.post().uri("/inventario/asignaciones/consumibles").body(request).retrieve()
                .body(StockConsumibleResponseDTO.class);
    }

    @Override
    public ActivoInventarioResponseDTO devolverActivo(DevolucionActivoRequestDTO request) {
        return clienteWeb.post().uri("/inventario/devoluciones/activos").body(request).retrieve()
                .body(ActivoInventarioResponseDTO.class);
    }

    @Override
    public StockConsumibleResponseDTO devolverConsumible(DevolucionConsumibleRequestDTO request) {
        return clienteWeb.post().uri("/inventario/devoluciones/consumibles").body(request).retrieve()
                .body(StockConsumibleResponseDTO.class);
    }

    @Override
    public ActivoInventarioResponseDTO trasladarActivo(TrasladoActivoRequestDTO request) {
        return clienteWeb.post().uri("/inventario/traslados/activos").body(request).retrieve()
                .body(ActivoInventarioResponseDTO.class);
    }

    @Override
    public StockConsumibleResponseDTO trasladarConsumible(TrasladoConsumibleRequestDTO request) {
        return clienteWeb.post().uri("/inventario/traslados/consumibles").body(request).retrieve()
                .body(StockConsumibleResponseDTO.class);
    }

    @Override
    public ActivoInventarioResponseDTO darBajaActivo(BajaActivoRequestDTO request) {
        return clienteWeb.post().uri("/inventario/bajas/activos").body(request).retrieve()
                .body(ActivoInventarioResponseDTO.class);
    }

    @Override
    public List<RecepcionLoteResponseDTO> listarRecepciones(Integer idOrdenCompra) {
        return clienteWeb.get()
                .uri("/ordenes-compra/{idOC}/recepciones", idOrdenCompra)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RecepcionLoteResponseDTO>>() {});
    }

    @Override
    public RecepcionLoteResponseDTO registrarRecepcionStock(Integer idOrdenCompra, Integer idDetalle,
                                                            RegistrarRecepcionStockRequestDTO request) {
        return clienteWeb.post()
                .uri("/ordenes-compra/{idOC}/detalles/{idDetalle}/recepciones/stock", idOrdenCompra, idDetalle)
                .body(request)
                .retrieve()
                .body(RecepcionLoteResponseDTO.class);
    }

    @Override
    public RecepcionLoteResponseDTO registrarRecepcionActivo(Integer idOrdenCompra, Integer idDetalle,
                                                             RegistrarRecepcionActivoRequestDTO request) {
        return clienteWeb.post()
                .uri("/ordenes-compra/{idOC}/detalles/{idDetalle}/recepciones/activo", idOrdenCompra, idDetalle)
                .body(request)
                .retrieve()
                .body(RecepcionLoteResponseDTO.class);
    }
}
