package com.uisrael.gestionactivosapi.presentacion.controladores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.uisrael.gestionactivosapi.aplicacion.servicios.InventarioService;
import com.uisrael.gestionactivosapi.aplicacion.servicios.ReparacionOrquestadorService;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.AsignacionActivosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.ConsumibleResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.MovimientoInventarioResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.RecepcionLoteResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.StockConsumibleResponseDTO;

@ExtendWith(MockitoExtension.class)
class InventarioControladorTest {

    @Mock private InventarioService inventarioService;
    @Mock private ReparacionOrquestadorService reparacionOrquestador;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new InventarioControlador(inventarioService, reparacionOrquestador)).build();
    }

    // ── Bodegas ──────────────────────────────────────────────

    @Test
    void listarBodegas_devuelveLista() throws Exception {
        BodegaResponseDTO b = new BodegaResponseDTO();
        b.setIdBodega(1);
        when(inventarioService.listarBodegas()).thenReturn(List.of(b));

        mockMvc.perform(get("/api/inventario/bodegas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idBodega").value(1));
    }

    @Test
    void crearBodega_devuelve201() throws Exception {
        BodegaResponseDTO creada = new BodegaResponseDTO();
        creada.setIdBodega(5);
        when(inventarioService.crearBodega(any())).thenReturn(creada);

        mockMvc.perform(post("/api/inventario/bodegas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"BOD-1\",\"nombre\":\"Bodega Central\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idBodega").value(5));
    }

    @Test
    void crearBodega_devuelve400SiFaltaNombre() throws Exception {
        mockMvc.perform(post("/api/inventario/bodegas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"BOD-1\"}"))
                .andExpect(status().isBadRequest());

        verify(inventarioService, never()).crearBodega(any());
    }

    // ── Consumibles ──────────────────────────────────────────

    @Test
    void listarConsumibles_devuelveLista() throws Exception {
        ConsumibleResponseDTO c = new ConsumibleResponseDTO();
        c.setIdConsumible(2);
        when(inventarioService.listarConsumibles()).thenReturn(List.of(c));

        mockMvc.perform(get("/api/inventario/consumibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idConsumible").value(2));
    }

    @Test
    void crearConsumible_devuelve201() throws Exception {
        ConsumibleResponseDTO creado = new ConsumibleResponseDTO();
        creado.setIdConsumible(9);
        when(inventarioService.crearConsumible(any())).thenReturn(creado);

        mockMvc.perform(post("/api/inventario/consumibles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"CONS-1\",\"nombre\":\"Toner\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idConsumible").value(9));
    }

    @Test
    void crearConsumible_devuelve400SiFaltaCodigo() throws Exception {
        mockMvc.perform(post("/api/inventario/consumibles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Toner\"}"))
                .andExpect(status().isBadRequest());

        verify(inventarioService, never()).crearConsumible(any());
    }

    @Test
    void actualizarConsumible_devuelve200() throws Exception {
        ConsumibleResponseDTO actualizado = new ConsumibleResponseDTO();
        actualizado.setIdConsumible(9);
        when(inventarioService.actualizarConsumible(eq(9), any())).thenReturn(actualizado);

        mockMvc.perform(put("/api/inventario/consumibles/9").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"CONS-1\",\"nombre\":\"Toner XL\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idConsumible").value(9));
    }

    @Test
    void cambiarEstadoConsumible_pasaEstadoBooleanoAlServicio() throws Exception {
        ConsumibleResponseDTO actualizado = new ConsumibleResponseDTO();
        actualizado.setIdConsumible(9);
        when(inventarioService.cambiarEstadoConsumible(9, false)).thenReturn(actualizado);

        mockMvc.perform(put("/api/inventario/consumibles/9/estado").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\": false}"))
                .andExpect(status().isOk());

        verify(inventarioService).cambiarEstadoConsumible(9, false);
    }

    // ── Ordenes de compra ────────────────────────────────────

    @Test
    void listarOrdenesCompra_devuelveLista() throws Exception {
        OrdenCompraResponseDTO oc = new OrdenCompraResponseDTO();
        oc.setIdOrdenCompra(1);
        when(inventarioService.listarOrdenesCompra()).thenReturn(List.of(oc));

        mockMvc.perform(get("/api/inventario/ordenes-compra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idOrdenCompra").value(1));
    }

    @Test
    void crearOrdenCompra_devuelve201() throws Exception {
        OrdenCompraResponseDTO creada = new OrdenCompraResponseDTO();
        creada.setIdOrdenCompra(3);
        when(inventarioService.crearOrdenCompra(any())).thenReturn(creada);

        mockMvc.perform(post("/api/inventario/ordenes-compra").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numeroOc\":\"OC-001\",\"bodegaDestinoId\":1,\"detalles\":[]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idOrdenCompra").value(3));
    }

    @Test
    void crearOrdenCompra_devuelve400SiFaltaBodegaDestino() throws Exception {
        mockMvc.perform(post("/api/inventario/ordenes-compra").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numeroOc\":\"OC-001\",\"detalles\":[]}"))
                .andExpect(status().isBadRequest());

        verify(inventarioService, never()).crearOrdenCompra(any());
    }

    @Test
    void obtenerOrdenCompra_devuelveOrden() throws Exception {
        OrdenCompraResponseDTO oc = new OrdenCompraResponseDTO();
        oc.setIdOrdenCompra(7);
        when(inventarioService.obtenerOrdenCompra(7)).thenReturn(oc);

        mockMvc.perform(get("/api/inventario/ordenes-compra/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrdenCompra").value(7));
    }

    @Test
    void listarRecepciones_devuelveListaDeLotes() throws Exception {
        RecepcionLoteResponseDTO lote = new RecepcionLoteResponseDTO();
        when(inventarioService.listarRecepcionesPorOrden(7)).thenReturn(List.of(lote));

        mockMvc.perform(get("/api/inventario/ordenes-compra/7/recepciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void recibirStockPorDetalle_devuelve201() throws Exception {
        RecepcionLoteResponseDTO lote = new RecepcionLoteResponseDTO();
        when(inventarioService.registrarRecepcionStockPorDetalle(eq(7), eq(2), any())).thenReturn(lote);

        mockMvc.perform(post("/api/inventario/ordenes-compra/7/detalles/2/recepciones/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idBodegaDestino\":1,\"cantidad\":5,\"recepcionadoPor\":\"Juan\"}"))
                .andExpect(status().isCreated());

        verify(inventarioService).registrarRecepcionStockPorDetalle(eq(7), eq(2), any());
    }

    @Test
    void recibirStockPorDetalle_devuelve400SiFaltaCantidad() throws Exception {
        mockMvc.perform(post("/api/inventario/ordenes-compra/7/detalles/2/recepciones/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idBodegaDestino\":1,\"recepcionadoPor\":\"Juan\"}"))
                .andExpect(status().isBadRequest());

        verify(inventarioService, never()).registrarRecepcionStockPorDetalle(anyInt(), anyInt(), any());
    }

    @Test
    void recibirActivoPorDetalle_devuelve201() throws Exception {
        RecepcionLoteResponseDTO lote = new RecepcionLoteResponseDTO();
        when(inventarioService.registrarRecepcionActivoPorDetalle(eq(7), eq(3), any())).thenReturn(lote);

        String body = """
                {"idBodegaDestino":1,"idCategoria":2,"idMarca":3,"modelo":"ThinkPad",
                 "serial":"SN-1","recepcionadoPor":"Juan"}
                """;

        mockMvc.perform(post("/api/inventario/ordenes-compra/7/detalles/3/recepciones/activo")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        verify(inventarioService).registrarRecepcionActivoPorDetalle(eq(7), eq(3), any());
    }

    @Test
    void recibirActivoPorDetalle_devuelve400SiFaltaSerial() throws Exception {
        String body = """
                {"idBodegaDestino":1,"idCategoria":2,"idMarca":3,"modelo":"ThinkPad","recepcionadoPor":"Juan"}
                """;

        mockMvc.perform(post("/api/inventario/ordenes-compra/7/detalles/3/recepciones/activo")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void confirmarRecepcionOrden_devuelveOrdenActualizada() throws Exception {
        OrdenCompraResponseDTO oc = new OrdenCompraResponseDTO();
        oc.setIdOrdenCompra(7);
        when(inventarioService.confirmarRecepcionOrden(7)).thenReturn(oc);

        mockMvc.perform(post("/api/inventario/ordenes-compra/7/confirmar-recepcion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrdenCompra").value(7));
    }

    @Test
    void cancelarOrdenCompra_devuelveOrdenCancelada() throws Exception {
        OrdenCompraResponseDTO oc = new OrdenCompraResponseDTO();
        oc.setIdOrdenCompra(7);
        when(inventarioService.cancelarOrdenCompra(7)).thenReturn(oc);

        mockMvc.perform(post("/api/inventario/ordenes-compra/7/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrdenCompra").value(7));
    }

    // ── Stock y movimientos ──────────────────────────────────

    @Test
    void listarStockPorBodega_devuelveLista() throws Exception {
        StockConsumibleResponseDTO stock = new StockConsumibleResponseDTO();
        stock.setIdStockConsumibleBodega(1);
        when(inventarioService.listarStockPorBodega(4)).thenReturn(List.of(stock));

        mockMvc.perform(get("/api/inventario/bodegas/4/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idStockConsumibleBodega").value(1));
    }

    @Test
    void listarMovimientosRecientes_devuelveLista() throws Exception {
        MovimientoInventarioResponseDTO mov = new MovimientoInventarioResponseDTO();
        mov.setIdMovimientoInventario(1);
        when(inventarioService.listarMovimientosRecientes()).thenReturn(List.of(mov));

        mockMvc.perform(get("/api/inventario/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMovimientoInventario").value(1));
    }

    @Test
    void obtenerMovimiento_devuelveMovimiento() throws Exception {
        MovimientoInventarioResponseDTO mov = new MovimientoInventarioResponseDTO();
        mov.setIdMovimientoInventario(8);
        when(inventarioService.obtenerMovimiento(8)).thenReturn(mov);

        mockMvc.perform(get("/api/inventario/movimientos/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMovimientoInventario").value(8));
    }

    @Test
    void buscarMovimientos_pasaParametrosAlServicioYDevuelvePagina() throws Exception {
        MovimientoInventarioResponseDTO mov = new MovimientoInventarioResponseDTO();
        mov.setIdMovimientoInventario(1);
        when(inventarioService.buscarMovimientosPaginados(1, 20, "ASIGNACION", "2024-01-01", "2024-02-01", "CRES-1"))
                .thenReturn(new PageImpl<>(List.of(mov), PageRequest.of(1, 20), 1));

        mockMvc.perform(get("/api/inventario/movimientos/buscar")
                        .param("page", "1").param("size", "20").param("tipo", "ASIGNACION")
                        .param("fechaDesde", "2024-01-01").param("fechaHasta", "2024-02-01")
                        .param("equipoCodigo", "CRES-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idMovimientoInventario").value(1));
    }

    @Test
    void buscarMovimientos_usaValoresPorDefectoDePaginacion() throws Exception {
        when(inventarioService.buscarMovimientosPaginados(0, 50, null, null, null, null))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 50), 0));

        mockMvc.perform(get("/api/inventario/movimientos/buscar"))
                .andExpect(status().isOk());
    }

    // ── Listados de activos ──────────────────────────────────

    @Test
    void listarActivosEnBodega_devuelveLista() throws Exception {
        ActivoInventarioResponseDTO a = new ActivoInventarioResponseDTO();
        a.setIdEquipo(1);
        when(inventarioService.listarActivosEnBodega()).thenReturn(List.of(a));

        mockMvc.perform(get("/api/inventario/activos/en-bodega"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEquipo").value(1));
    }

    @Test
    void listarActivosEnReparacion_consultaEstadoCorrecto() throws Exception {
        when(inventarioService.listarActivosPorEstado("EN_REPARACION")).thenReturn(List.of());

        mockMvc.perform(get("/api/inventario/activos/en-reparacion")).andExpect(status().isOk());

        verify(inventarioService).listarActivosPorEstado("EN_REPARACION");
    }

    @Test
    void listarActivosAsignados_consultaEstadoCorrecto() throws Exception {
        when(inventarioService.listarActivosPorEstado("ASIGNADO")).thenReturn(List.of());

        mockMvc.perform(get("/api/inventario/activos/asignados")).andExpect(status().isOk());

        verify(inventarioService).listarActivosPorEstado("ASIGNADO");
    }

    @Test
    void listarActivosEnTransito_devuelveLista() throws Exception {
        when(inventarioService.listarActivosEnTransito()).thenReturn(List.of());

        mockMvc.perform(get("/api/inventario/activos/en-transito")).andExpect(status().isOk());
    }

    @Test
    void listarSinInventario_devuelveLista() throws Exception {
        when(inventarioService.listarSinInventario()).thenReturn(List.of());

        mockMvc.perform(get("/api/inventario/activos/sin-inventario")).andExpect(status().isOk());
    }

    // ── Asignaciones ─────────────────────────────────────────

    @Test
    void asignarActivosLote_devuelve201() throws Exception {
        AsignacionActivosResponseDTO resp = new AsignacionActivosResponseDTO();
        when(inventarioService.asignarActivosLote(any())).thenReturn(resp);

        mockMvc.perform(post("/api/inventario/asignaciones/activos/lote").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipoIds\":[1,2],\"custodioId\":5}"))
                .andExpect(status().isCreated());
    }

    @Test
    void asignarActivosLote_devuelve400SiFaltaCustodio() throws Exception {
        mockMvc.perform(post("/api/inventario/asignaciones/activos/lote").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipoIds\":[1,2]}"))
                .andExpect(status().isBadRequest());

        verify(inventarioService, never()).asignarActivosLote(any());
    }

    @Test
    void asignarActivo_devuelve201() throws Exception {
        ActivoInventarioResponseDTO a = new ActivoInventarioResponseDTO();
        a.setIdEquipo(1);
        when(inventarioService.asignarActivo(any())).thenReturn(a);

        mockMvc.perform(post("/api/inventario/asignaciones/activos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipoId\":1,\"custodioId\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEquipo").value(1));
    }

    @Test
    void asignarConsumible_devuelve201() throws Exception {
        StockConsumibleResponseDTO stock = new StockConsumibleResponseDTO();
        when(inventarioService.asignarConsumible(any())).thenReturn(stock);

        mockMvc.perform(post("/api/inventario/asignaciones/consumibles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bodegaId\":1,\"consumibleId\":2,\"custodioId\":5,\"cantidad\":3}"))
                .andExpect(status().isCreated());
    }

    @Test
    void asignarConsumible_devuelve400SiCantidadEsCero() throws Exception {
        mockMvc.perform(post("/api/inventario/asignaciones/consumibles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bodegaId\":1,\"consumibleId\":2,\"custodioId\":5,\"cantidad\":0}"))
                .andExpect(status().isBadRequest());

        verify(inventarioService, never()).asignarConsumible(any());
    }

    // ── Devoluciones ─────────────────────────────────────────

    @Test
    void devolverActivo_devuelve201() throws Exception {
        ActivoInventarioResponseDTO a = new ActivoInventarioResponseDTO();
        when(inventarioService.devolverActivo(any())).thenReturn(a);

        mockMvc.perform(post("/api/inventario/devoluciones/activos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipoId\":1,\"bodegaId\":2}"))
                .andExpect(status().isCreated());
    }

    @Test
    void devolverConsumible_devuelve201() throws Exception {
        StockConsumibleResponseDTO stock = new StockConsumibleResponseDTO();
        when(inventarioService.devolverConsumible(any())).thenReturn(stock);

        mockMvc.perform(post("/api/inventario/devoluciones/consumibles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bodegaId\":1,\"consumibleId\":2,\"custodioId\":5,\"cantidad\":3}"))
                .andExpect(status().isCreated());
    }

    // ── Traslados ────────────────────────────────────────────

    @Test
    void trasladarActivo_devuelve201() throws Exception {
        ActivoInventarioResponseDTO a = new ActivoInventarioResponseDTO();
        when(inventarioService.trasladarActivo(any())).thenReturn(a);

        mockMvc.perform(post("/api/inventario/traslados/activos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipoId\":1,\"bodegaDestinoId\":2}"))
                .andExpect(status().isCreated());
    }

    @Test
    void trasladarConsumible_devuelve201() throws Exception {
        StockConsumibleResponseDTO stock = new StockConsumibleResponseDTO();
        when(inventarioService.trasladarConsumible(any())).thenReturn(stock);

        mockMvc.perform(post("/api/inventario/traslados/consumibles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bodegaOrigenId\":1,\"bodegaDestinoId\":2,\"consumibleId\":3,\"cantidad\":4}"))
                .andExpect(status().isCreated());
    }

    @Test
    void confirmarLlegadaActivo_devuelve200() throws Exception {
        ActivoInventarioResponseDTO a = new ActivoInventarioResponseDTO();
        when(inventarioService.confirmarLlegadaActivo(any())).thenReturn(a);

        mockMvc.perform(post("/api/inventario/activos/traslado/confirmar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipoId\":1,\"bodegaDestinoId\":2}"))
                .andExpect(status().isOk());
    }

    // ── Bajas ────────────────────────────────────────────────

    @Test
    void darBajaActivo_devuelve201() throws Exception {
        ActivoInventarioResponseDTO a = new ActivoInventarioResponseDTO();
        when(inventarioService.darBajaActivo(any())).thenReturn(a);

        mockMvc.perform(post("/api/inventario/bajas/activos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipoId\":1,\"motivo\":\"DANIO_IRREPARABLE\",\"observacion\":\"Sin reparacion posible\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void darBajaActivo_devuelve400SiFaltaMotivo() throws Exception {
        mockMvc.perform(post("/api/inventario/bajas/activos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipoId\":1,\"observacion\":\"Sin reparacion posible\"}"))
                .andExpect(status().isBadRequest());

        verify(inventarioService, never()).darBajaActivo(any());
    }

    // ── Reparaciones ─────────────────────────────────────────

    @Test
    void enviarAReparacion_devuelve200() throws Exception {
        ActivoInventarioResponseDTO a = new ActivoInventarioResponseDTO();
        when(inventarioService.enviarAReparacion(any())).thenReturn(a);

        mockMvc.perform(post("/api/inventario/activos/reparacion/enviar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipoId\":1}"))
                .andExpect(status().isOk());
    }

    @Test
    void retornarDeReparacion_devuelve200() throws Exception {
        ActivoInventarioResponseDTO a = new ActivoInventarioResponseDTO();
        when(inventarioService.retornarDeReparacion(any())).thenReturn(a);

        mockMvc.perform(post("/api/inventario/activos/reparacion/retornar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipoId\":1,\"bodegaDestinoId\":2}"))
                .andExpect(status().isOk());
    }

    @Test
    void enviarConOt_pasaCorreoDelPrincipalAlOrquestador() throws Exception {
        ActivoInventarioResponseDTO a = new ActivoInventarioResponseDTO();
        when(reparacionOrquestador.enviarConOt(any(), eq("tecnico@cresio.com"))).thenReturn(a);
        Principal principal = () -> "tecnico@cresio.com";

        mockMvc.perform(post("/api/inventario/activos/reparacion/enviar-con-ot")
                        .contentType(MediaType.APPLICATION_JSON).principal(principal)
                        .content("{\"equipoId\":1,\"custodioId\":5}"))
                .andExpect(status().isOk());

        verify(reparacionOrquestador).enviarConOt(any(), eq("tecnico@cresio.com"));
    }

    @Test
    void enviarConOt_correoEsNullSiNoHayPrincipal() throws Exception {
        ActivoInventarioResponseDTO a = new ActivoInventarioResponseDTO();
        when(reparacionOrquestador.enviarConOt(any(), isNull())).thenReturn(a);

        mockMvc.perform(post("/api/inventario/activos/reparacion/enviar-con-ot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipoId\":1,\"custodioId\":5}"))
                .andExpect(status().isOk());

        verify(reparacionOrquestador).enviarConOt(any(), isNull());
    }

    @Test
    void retornarYCerrar_pasaCorreoDelPrincipalAlOrquestador() throws Exception {
        ActivoInventarioResponseDTO a = new ActivoInventarioResponseDTO();
        when(reparacionOrquestador.retornarYCerrar(any(), eq("tecnico@cresio.com"))).thenReturn(a);
        Principal principal = () -> "tecnico@cresio.com";

        mockMvc.perform(post("/api/inventario/activos/reparacion/retornar-y-cerrar")
                        .contentType(MediaType.APPLICATION_JSON).principal(principal)
                        .content("{\"equipoId\":1,\"bodegaDestinoId\":2,\"resultadoTecnico\":\"Reparado\"}"))
                .andExpect(status().isOk());

        verify(reparacionOrquestador).retornarYCerrar(any(), eq("tecnico@cresio.com"));
    }

    @Test
    void retornarYCerrar_devuelve400SiFaltaResultadoTecnico() throws Exception {
        mockMvc.perform(post("/api/inventario/activos/reparacion/retornar-y-cerrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipoId\":1,\"bodegaDestinoId\":2}"))
                .andExpect(status().isBadRequest());

        verify(reparacionOrquestador, never()).retornarYCerrar(any(), any());
    }

    // ── Etiqueta y adopcion de inventario inicial ───────────

    @Test
    void registrarEtiqueta_devuelve200() throws Exception {
        ActivoInventarioResponseDTO a = new ActivoInventarioResponseDTO();
        a.setIdEquipo(1);
        when(inventarioService.registrarEtiqueta(eq(1), any())).thenReturn(a);

        mockMvc.perform(patch("/api/inventario/activos/1/etiqueta").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"etiquetado\":true,\"codigoCresio\":\"CRES-0001\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEquipo").value(1));
    }

    @Test
    void adoptarInventarioInicial_devuelve200() throws Exception {
        ActivoInventarioResponseDTO a = new ActivoInventarioResponseDTO();
        a.setIdEquipo(1);
        when(inventarioService.adoptarInventarioInicial(eq(1), any())).thenReturn(a);

        mockMvc.perform(patch("/api/inventario/activos/1/adoptar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bodegaId\":2,\"etiquetado\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEquipo").value(1));
    }
}
