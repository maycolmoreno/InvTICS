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
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AsignacionLoteRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AsignacionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.BajaActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.ConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.DevolucionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.DevolucionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.EnviarReparacionRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.OrdenCompraRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RetornarReparacionRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.ConfirmarLlegadaActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AdoptarInventarioInicialRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RegistrarEtiquetaRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.AsignacionActivosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.MovimientoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.MovimientoPageResponseDTO;
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
    public ConsumibleResponseDTO actualizarConsumible(Integer idConsumible, ConsumibleRequestDTO request) {
        return clienteWeb.put().uri("/inventario/consumibles/{id}", idConsumible).body(request).retrieve()
                .body(ConsumibleResponseDTO.class);
    }

    @Override
    public ConsumibleResponseDTO cambiarEstadoConsumible(Integer idConsumible, boolean estado) {
        ConsumibleRequestDTO request = new ConsumibleRequestDTO();
        request.setEstado(estado);
        return clienteWeb.put().uri("/inventario/consumibles/{id}/estado", idConsumible).body(request).retrieve()
                .body(ConsumibleResponseDTO.class);
    }

    @Override
    public List<OrdenCompraResponseDTO> listarOrdenesCompra() {
        return clienteWeb.get().uri("/inventario/ordenes-compra").retrieve()
                .body(new ParameterizedTypeReference<List<OrdenCompraResponseDTO>>() {});
    }

    @Override
    public OrdenCompraResponseDTO obtenerOrdenCompra(Integer id) {
        return clienteWeb.get().uri("/inventario/ordenes-compra/{id}", id).retrieve().body(OrdenCompraResponseDTO.class);
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
    public OrdenCompraResponseDTO cancelarOrdenCompra(Integer ordenCompraId) {
        return clienteWeb.post().uri("/inventario/ordenes-compra/{id}/cancelar", ordenCompraId)
                .retrieve()
                .body(OrdenCompraResponseDTO.class);
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
    public MovimientoPageResponseDTO buscarMovimientos(Integer page, Integer size, String tipo,
            String fechaDesde, String fechaHasta, String equipoCodigo) {
        StringBuilder uri = new StringBuilder("/inventario/movimientos/buscar?");
        uri.append("page=").append(page != null ? page : 0);
        uri.append("&size=").append(size != null ? size : 50);
        if (tipo != null && !tipo.isBlank()) {
            try { uri.append("&tipo=").append(java.net.URLEncoder.encode(tipo, "UTF-8")); }
            catch (Exception ignored) { uri.append("&tipo=").append(tipo); }
        }
        if (fechaDesde != null && !fechaDesde.isBlank()) uri.append("&fechaDesde=").append(fechaDesde);
        if (fechaHasta != null && !fechaHasta.isBlank()) uri.append("&fechaHasta=").append(fechaHasta);
        if (equipoCodigo != null && !equipoCodigo.isBlank()) {
            try { uri.append("&equipoCodigo=").append(java.net.URLEncoder.encode(equipoCodigo, "UTF-8")); }
            catch (Exception ignored) { uri.append("&equipoCodigo=").append(equipoCodigo); }
        }
        try {
            return clienteWeb.get().uri(uri.toString()).retrieve()
                    .body(MovimientoPageResponseDTO.class);
        } catch (Exception ex) {
            // Fallback: wrap list response in a page for backward compatibility
            MovimientoPageResponseDTO fallback = new MovimientoPageResponseDTO();
            try {
                List<MovimientoInventarioResponseDTO> todos = listarMovimientosRecientes();
                fallback.setContent(todos != null ? todos : List.of());
                fallback.setTotalElements(todos != null ? todos.size() : 0);
            } catch (Exception ignored) {
                fallback.setContent(List.of());
                fallback.setTotalElements(0);
            }
            fallback.setTotalPages(1);
            fallback.setNumber(0);
            fallback.setSize(fallback.getContent().size());
            fallback.setFirst(true);
            fallback.setLast(true);
            return fallback;
        }
    }

    @Override
    public List<ActivoInventarioResponseDTO> listarActivosEnBodega() {
        return clienteWeb.get().uri("/inventario/activos/en-bodega").retrieve()
                .body(new ParameterizedTypeReference<List<ActivoInventarioResponseDTO>>() {});
    }

    @Override
    public List<ActivoInventarioResponseDTO> listarActivosAsignados() {
        return clienteWeb.get().uri("/inventario/activos/asignados").retrieve()
                .body(new ParameterizedTypeReference<List<ActivoInventarioResponseDTO>>() {});
    }

    @Override
    public ActivoInventarioResponseDTO asignarActivo(AsignacionActivoRequestDTO request) {
        return clienteWeb.post().uri("/inventario/asignaciones/activos").body(request).retrieve()
                .body(ActivoInventarioResponseDTO.class);
    }

    @Override
    public AsignacionActivosResponseDTO asignarActivosLote(AsignacionLoteRequestDTO request) {
        return clienteWeb.post().uri("/inventario/asignaciones/activos/lote").body(request).retrieve()
                .body(AsignacionActivosResponseDTO.class);
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
    public ActivoInventarioResponseDTO enviarAReparacion(EnviarReparacionRequestDTO request) {
        return clienteWeb.post().uri("/inventario/activos/reparacion/enviar").body(request).retrieve()
                .body(ActivoInventarioResponseDTO.class);
    }

    @Override
    public ActivoInventarioResponseDTO retornarDeReparacion(RetornarReparacionRequestDTO request) {
        return clienteWeb.post().uri("/inventario/activos/reparacion/retornar").body(request).retrieve()
                .body(ActivoInventarioResponseDTO.class);
    }

    @Override
    public List<ActivoInventarioResponseDTO> listarActivosEnReparacion() {
        return clienteWeb.get().uri("/inventario/activos/en-reparacion").retrieve()
                .body(new ParameterizedTypeReference<List<ActivoInventarioResponseDTO>>() {});
    }

    @Override
    public List<ActivoInventarioResponseDTO> listarActivosEnTransito() {
        return clienteWeb.get().uri("/inventario/activos/en-transito").retrieve()
                .body(new ParameterizedTypeReference<List<ActivoInventarioResponseDTO>>() {});
    }

    @Override
    public ActivoInventarioResponseDTO confirmarLlegadaActivo(ConfirmarLlegadaActivoRequestDTO request) {
        return clienteWeb.post().uri("/inventario/activos/traslado/confirmar").body(request).retrieve()
                .body(ActivoInventarioResponseDTO.class);
    }

    @Override
    public List<RecepcionLoteResponseDTO> listarRecepciones(Integer idOrdenCompra) {
        return clienteWeb.get()
                .uri("/inventario/ordenes-compra/{idOC}/recepciones", idOrdenCompra)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RecepcionLoteResponseDTO>>() {});
    }

    @Override
    public RecepcionLoteResponseDTO registrarRecepcionStock(Integer idOrdenCompra, Integer idDetalle,
                                                            RegistrarRecepcionStockRequestDTO request) {
        return clienteWeb.post()
                .uri("/inventario/ordenes-compra/{idOC}/detalles/{idDetalle}/recepciones/stock", idOrdenCompra, idDetalle)
                .body(request)
                .retrieve()
                .body(RecepcionLoteResponseDTO.class);
    }

    @Override
    public RecepcionLoteResponseDTO registrarRecepcionActivo(Integer idOrdenCompra, Integer idDetalle,
                                                             RegistrarRecepcionActivoRequestDTO request) {
        return clienteWeb.post()
                .uri("/inventario/ordenes-compra/{idOC}/detalles/{idDetalle}/recepciones/activo", idOrdenCompra, idDetalle)
                .body(request)
                .retrieve()
                .body(RecepcionLoteResponseDTO.class);
    }

    @Override
    public ActivoInventarioResponseDTO registrarEtiqueta(Integer idEquipo, RegistrarEtiquetaRequestDTO request) {
        return clienteWeb.patch()
                .uri("/inventario/activos/{id}/etiqueta", idEquipo)
                .body(request)
                .retrieve()
                .body(ActivoInventarioResponseDTO.class);
    }

    @Override
    public List<ActivoInventarioResponseDTO> listarSinInventario() {
        return clienteWeb.get()
                .uri("/inventario/activos/sin-inventario")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ActivoInventarioResponseDTO>>() {});
    }

    @Override
    public ActivoInventarioResponseDTO adoptarInventarioInicial(Integer idEquipo, AdoptarInventarioInicialRequestDTO request) {
        return clienteWeb.patch()
                .uri("/inventario/activos/{id}/adoptar", idEquipo)
                .body(request)
                .retrieve()
                .body(ActivoInventarioResponseDTO.class);
    }
}
