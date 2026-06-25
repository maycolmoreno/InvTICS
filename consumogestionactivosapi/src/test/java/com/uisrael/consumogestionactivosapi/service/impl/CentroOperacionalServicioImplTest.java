package com.uisrael.consumogestionactivosapi.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.MovimientoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.MovimientoPageResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.StockConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional.CentroOperacionalDTO;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;

@ExtendWith(MockitoExtension.class)
class CentroOperacionalServicioImplTest {

    @Mock private IInventarioOperacionServicio inventarioOperacionServicio;
    @Mock private IEquiposServicio equiposServicio;
    @Mock private ICustodiasServicio custodiasServicio;

    @Test
    void obtenerCentroOperacional_componeBandejasRiesgosYTimeline() {
        CentroOperacionalServicioImpl servicio = new CentroOperacionalServicioImpl(
                inventarioOperacionServicio, equiposServicio, custodiasServicio);

        OrdenCompraResponseDTO oc = new OrdenCompraResponseDTO();
        oc.setEstado("EMITIDA");
        ActivoInventarioResponseDTO transito = new ActivoInventarioResponseDTO();
        ActivoInventarioResponseDTO sinEtiqueta = new ActivoInventarioResponseDTO();
        sinEtiqueta.setEtiquetado(false);
        ActivoInventarioResponseDTO reparacion = new ActivoInventarioResponseDTO();
        BodegaResponseDTO bodega = new BodegaResponseDTO();
        bodega.setIdBodega(7);
        bodega.setEstado(true);
        StockConsumibleResponseDTO stock = new StockConsumibleResponseDTO();
        stock.setCantidad(2);
        EquiposResponseDTO equipo = new EquiposResponseDTO();
        equipo.setIdEquipo(11);
        equipo.setEstado(true);
        MovimientoInventarioResponseDTO movimiento = new MovimientoInventarioResponseDTO();
        movimiento.setTipoMovimiento("ASIGNACION");
        movimiento.setEquipoCodigo("CR-LAP-001");
        movimiento.setFechaMovimiento(LocalDateTime.now());
        MovimientoPageResponseDTO pagina = new MovimientoPageResponseDTO();
        pagina.setContent(List.of(movimiento));

        when(inventarioOperacionServicio.listarOrdenesCompra()).thenReturn(List.of(oc));
        when(inventarioOperacionServicio.listarActivosEnTransito()).thenReturn(List.of(transito));
        when(inventarioOperacionServicio.listarActivosEnBodega()).thenReturn(List.of(sinEtiqueta));
        when(inventarioOperacionServicio.listarActivosEnReparacion()).thenReturn(List.of(reparacion));
        when(inventarioOperacionServicio.listarBodegas()).thenReturn(List.of(bodega));
        when(inventarioOperacionServicio.listarStockPorBodega(7)).thenReturn(List.of(stock));
        when(inventarioOperacionServicio.buscarMovimientos(0, 10, null, null, null, null)).thenReturn(pagina);
        when(equiposServicio.listarEquipos()).thenReturn(List.of(equipo));
        when(custodiasServicio.listarCustodias()).thenReturn(List.of(new CustodiasResponseDTO()));

        CentroOperacionalDTO centro = servicio.obtenerCentroOperacional();

        assertEquals(7, centro.getBandejas().size());
        assertEquals(1, cantidad(centro, "RECEPCIONES_PENDIENTES"));
        assertEquals(1, cantidad(centro, "TRASLADOS_PENDIENTES"));
        assertEquals(1, cantidad(centro, "STOCK_CRITICO"));
        assertEquals(1, cantidad(centro, "ACTIVOS_SIN_CUSTODIO"));
        assertEquals(1, cantidad(centro, "ACTIVOS_SIN_ETIQUETA"));
        assertEquals(1, centro.getMovimientosRecientes().size());
        assertEquals(4, centro.getRiesgos().size());
    }

    private long cantidad(CentroOperacionalDTO centro, String tipo) {
        return centro.getBandejas().stream()
                .filter(b -> tipo.equals(b.getTipo()))
                .findFirst()
                .orElseThrow()
                .getCantidad();
    }
}
