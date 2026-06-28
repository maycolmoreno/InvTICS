package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.servicios.InventarioService;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.AsignacionActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.AsignacionLoteRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.AsignacionConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.BajaActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.BodegaRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.ConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.DevolucionActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.DevolucionConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.ConfirmarLlegadaActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.EnviarReparacionRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.OrdenCompraRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RetornarReparacionRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.TrasladoActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.TrasladoConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.AsignacionActivosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.ConsumibleResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.MovimientoInventarioResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.AdoptarInventarioInicialRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RegistrarEtiquetaRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RegistrarRecepcionActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RegistrarRecepcionStockRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.RecepcionLoteResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.StockConsumibleResponseDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventario")
public class InventarioControlador {

    private final InventarioService inventarioService;

    public InventarioControlador(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/bodegas")
    public List<BodegaResponseDTO> listarBodegas() {
        return inventarioService.listarBodegas();
    }

    @PostMapping("/bodegas")
    public ResponseEntity<BodegaResponseDTO> crearBodega(@Valid @RequestBody BodegaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crearBodega(request));
    }

    @GetMapping("/consumibles")
    public List<ConsumibleResponseDTO> listarConsumibles() {
        return inventarioService.listarConsumibles();
    }

    @PostMapping("/consumibles")
    public ResponseEntity<ConsumibleResponseDTO> crearConsumible(@Valid @RequestBody ConsumibleRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crearConsumible(request));
    }

    @PutMapping("/consumibles/{id}")
    public ResponseEntity<ConsumibleResponseDTO> actualizarConsumible(
            @PathVariable Integer id,
            @Valid @RequestBody ConsumibleRequestDTO request) {
        return ResponseEntity.ok(inventarioService.actualizarConsumible(id, request));
    }

    @PutMapping("/consumibles/{id}/estado")
    public ResponseEntity<ConsumibleResponseDTO> cambiarEstadoConsumible(
            @PathVariable Integer id,
            @RequestBody ConsumibleRequestDTO request) {
        return ResponseEntity.ok(inventarioService.cambiarEstadoConsumible(id, Boolean.TRUE.equals(request.getEstado())));
    }

    @GetMapping("/ordenes-compra")
    public List<OrdenCompraResponseDTO> listarOrdenesCompra() {
        return inventarioService.listarOrdenesCompra();
    }

    @PostMapping("/ordenes-compra")
    public ResponseEntity<OrdenCompraResponseDTO> crearOrdenCompra(@Valid @RequestBody OrdenCompraRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crearOrdenCompra(request));
    }

    @GetMapping("/ordenes-compra/{id}")
    public ResponseEntity<OrdenCompraResponseDTO> obtenerOrdenCompra(@PathVariable Integer id) {
        return ResponseEntity.ok(inventarioService.obtenerOrdenCompra(id));
    }

    @GetMapping("/ordenes-compra/{idOC}/recepciones")
    public List<RecepcionLoteResponseDTO> listarRecepciones(@PathVariable Integer idOC) {
        return inventarioService.listarRecepcionesPorOrden(idOC);
    }

    @PostMapping("/ordenes-compra/{idOC}/detalles/{idDetalle}/recepciones/stock")
    public ResponseEntity<RecepcionLoteResponseDTO> recibirStockPorDetalle(
            @PathVariable Integer idOC,
            @PathVariable Integer idDetalle,
            @Valid @RequestBody RegistrarRecepcionStockRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.registrarRecepcionStockPorDetalle(idOC, idDetalle, request));
    }

    @PostMapping("/ordenes-compra/{idOC}/detalles/{idDetalle}/recepciones/activo")
    public ResponseEntity<RecepcionLoteResponseDTO> recibirActivoPorDetalle(
            @PathVariable Integer idOC,
            @PathVariable Integer idDetalle,
            @Valid @RequestBody RegistrarRecepcionActivoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.registrarRecepcionActivoPorDetalle(idOC, idDetalle, request));
    }

    @PostMapping("/ordenes-compra/{id}/confirmar-recepcion")
    public ResponseEntity<OrdenCompraResponseDTO> confirmarRecepcionOrden(@PathVariable Integer id) {
        return ResponseEntity.ok(inventarioService.confirmarRecepcionOrden(id));
    }

    @PostMapping("/ordenes-compra/{id}/cancelar")
    public ResponseEntity<OrdenCompraResponseDTO> cancelarOrdenCompra(@PathVariable Integer id) {
        return ResponseEntity.ok(inventarioService.cancelarOrdenCompra(id));
    }

    @GetMapping("/bodegas/{id}/stock")
    public List<StockConsumibleResponseDTO> listarStockPorBodega(@PathVariable Integer id) {
        return inventarioService.listarStockPorBodega(id);
    }

    @GetMapping("/movimientos")
    public List<MovimientoInventarioResponseDTO> listarMovimientosRecientes() {
        return inventarioService.listarMovimientosRecientes();
    }

    @GetMapping("/movimientos/buscar")
    public Page<MovimientoInventarioResponseDTO> buscarMovimientos(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(required = false) String equipoCodigo) {
        return inventarioService.buscarMovimientosPaginados(page, size, tipo, fechaDesde, fechaHasta, equipoCodigo);
    }

    @GetMapping("/activos/en-bodega")
    public List<ActivoInventarioResponseDTO> listarActivosEnBodega() {
        return inventarioService.listarActivosEnBodega();
    }

    @GetMapping("/activos/en-reparacion")
    public List<ActivoInventarioResponseDTO> listarActivosEnReparacion() {
        return inventarioService.listarActivosPorEstado("EN_REPARACION");
    }

    @GetMapping("/activos/asignados")
    public List<ActivoInventarioResponseDTO> listarActivosAsignados() {
        return inventarioService.listarActivosPorEstado("ASIGNADO");
    }

    @GetMapping("/activos/en-transito")
    public List<ActivoInventarioResponseDTO> listarActivosEnTransito() {
        return inventarioService.listarActivosEnTransito();
    }

    @PostMapping("/asignaciones/activos/lote")
    public ResponseEntity<AsignacionActivosResponseDTO> asignarActivosLote(
            @Valid @RequestBody AsignacionLoteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.asignarActivosLote(request));
    }

    @PostMapping("/asignaciones/activos")
    public ResponseEntity<ActivoInventarioResponseDTO> asignarActivo(
            @Valid @RequestBody AsignacionActivoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.asignarActivo(request));
    }

    @PostMapping("/asignaciones/consumibles")
    public ResponseEntity<StockConsumibleResponseDTO> asignarConsumible(
            @Valid @RequestBody AsignacionConsumibleRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.asignarConsumible(request));
    }

    @PostMapping("/devoluciones/activos")
    public ResponseEntity<ActivoInventarioResponseDTO> devolverActivo(
            @Valid @RequestBody DevolucionActivoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.devolverActivo(request));
    }

    @PostMapping("/devoluciones/consumibles")
    public ResponseEntity<StockConsumibleResponseDTO> devolverConsumible(
            @Valid @RequestBody DevolucionConsumibleRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.devolverConsumible(request));
    }

    @PostMapping("/traslados/activos")
    public ResponseEntity<ActivoInventarioResponseDTO> trasladarActivo(
            @Valid @RequestBody TrasladoActivoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.trasladarActivo(request));
    }

    @PostMapping("/traslados/consumibles")
    public ResponseEntity<StockConsumibleResponseDTO> trasladarConsumible(
            @Valid @RequestBody TrasladoConsumibleRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.trasladarConsumible(request));
    }

    @PostMapping("/bajas/activos")
    public ResponseEntity<ActivoInventarioResponseDTO> darBajaActivo(
            @Valid @RequestBody BajaActivoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.darBajaActivo(request));
    }

    @PostMapping("/activos/reparacion/enviar")
    public ResponseEntity<ActivoInventarioResponseDTO> enviarAReparacion(
            @Valid @RequestBody EnviarReparacionRequestDTO request) {
        return ResponseEntity.ok(inventarioService.enviarAReparacion(request));
    }

    @PostMapping("/activos/reparacion/retornar")
    public ResponseEntity<ActivoInventarioResponseDTO> retornarDeReparacion(
            @Valid @RequestBody RetornarReparacionRequestDTO request) {
        return ResponseEntity.ok(inventarioService.retornarDeReparacion(request));
    }

    @PostMapping("/activos/traslado/confirmar")
    public ResponseEntity<ActivoInventarioResponseDTO> confirmarLlegadaActivo(
            @Valid @RequestBody ConfirmarLlegadaActivoRequestDTO request) {
        return ResponseEntity.ok(inventarioService.confirmarLlegadaActivo(request));
    }

    @PatchMapping("/activos/{id}/etiqueta")
    public ResponseEntity<ActivoInventarioResponseDTO> registrarEtiqueta(
            @PathVariable Integer id,
            @RequestBody RegistrarEtiquetaRequestDTO request) {
        return ResponseEntity.ok(inventarioService.registrarEtiqueta(id, request));
    }

    @GetMapping("/activos/sin-inventario")
    public ResponseEntity<List<ActivoInventarioResponseDTO>> listarSinInventario() {
        return ResponseEntity.ok(inventarioService.listarSinInventario());
    }

    @PatchMapping("/activos/{id}/adoptar")
    public ResponseEntity<ActivoInventarioResponseDTO> adoptarInventarioInicial(
            @PathVariable Integer id,
            @RequestBody AdoptarInventarioInicialRequestDTO request) {
        return ResponseEntity.ok(inventarioService.adoptarInventarioInicial(id, request));
    }
}
