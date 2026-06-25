package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.MovimientoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.StockConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional.CentroOperacionalDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional.MovimientoRecienteDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional.QuickActionDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional.RiesgoOperativoDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional.WorkQueueResumenDTO;
import com.uisrael.consumogestionactivosapi.service.ICentroOperacionalServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CentroOperacionalServicioImpl implements ICentroOperacionalServicio {

    private static final int STOCK_CRITICO = 5;

    private final IInventarioOperacionServicio inventarioOperacionServicio;
    private final IEquiposServicio equiposServicio;
    private final ICustodiasServicio custodiasServicio;

    @Override
    public CentroOperacionalDTO obtenerCentroOperacional() {
        CentroOperacionalDTO centro = new CentroOperacionalDTO();

        List<OrdenCompraResponseDTO> ordenes = safe(() -> inventarioOperacionServicio.listarOrdenesCompra());
        List<ActivoInventarioResponseDTO> enTransito = safe(() -> inventarioOperacionServicio.listarActivosEnTransito());
        List<ActivoInventarioResponseDTO> enBodega = safe(() -> inventarioOperacionServicio.listarActivosEnBodega());
        List<ActivoInventarioResponseDTO> enReparacion = safe(() -> inventarioOperacionServicio.listarActivosEnReparacion());
        List<StockConsumibleResponseDTO> stock = stockDisponible();
        List<EquiposResponseDTO> equipos = safe(() -> equiposServicio.listarEquipos());
        List<CustodiasResponseDTO> custodias = safe(() -> custodiasServicio.listarCustodias());

        long recepcionesPendientes = ordenes.stream().filter(this::requiereRecepcion).count();
        long stockCritico = stock.stream()
                .filter(s -> s.getCantidad() != null && s.getCantidad() <= STOCK_CRITICO)
                .count();
        long activosSinEtiqueta = enBodega.stream().filter(a -> Boolean.FALSE.equals(a.getEtiquetado())).count();
        long activosSinCustodio = activosSinCustodio(equipos, custodias);

        centro.setBandejas(List.of(
                bandeja("RECEPCIONES_PENDIENTES", "Recepciones pendientes", "OC emitidas o parcialmente recibidas.",
                        recepcionesPendientes, "alta", "/inventario/compras", "Recibir", "feather-shopping-cart"),
                bandeja("TRASLADOS_PENDIENTES", "Traslados pendientes", "Activos en transito por confirmar.",
                        enTransito.size(), "alta", "/inventario/traslados", "Confirmar", "feather-repeat"),
                bandeja("STOCK_CRITICO", "Stock critico", "Consumibles con existencia menor o igual a 5.",
                        stockCritico, "media", "/inventario/stock", "Revisar stock", "feather-layers"),
                bandeja("ACTIVOS_SIN_CUSTODIO", "Activos sin custodio", "Equipos activos sin custodia vigente.",
                        activosSinCustodio, "media", "/equipos", "Asignar", "feather-user-x"),
                bandeja("ACTIVOS_SIN_ETIQUETA", "Activos sin etiqueta", "Activos en bodega que no pueden asignarse.",
                        activosSinEtiqueta, "alta", "/inventario/asignaciones", "Etiquetar", "feather-tag"),
                bandeja("REPARACIONES_ABIERTAS", "Reparaciones abiertas", "Activos actualmente en reparacion.",
                        enReparacion.size(), "media", "/inventario/reparaciones", "Dar seguimiento", "feather-tool"),
                bandeja("GARANTIAS_PROXIMAS", "Garantias proximas", "Sin datos de garantia expuestos todavia.",
                        0, "baja", "/inventario/dashboard", "Revisar", "feather-shield")));

        centro.setRiesgos(Stream.of(
                riesgoSi(activosSinEtiqueta > 0, "Activos sin etiqueta",
                        activosSinEtiqueta + " activos en bodega no deberian salir hasta etiquetarse.", "alta",
                        "/inventario/asignaciones"),
                riesgoSi(stockCritico > 0, "Stock critico",
                        stockCritico + " existencias requieren reposicion o traslado.", "media", "/inventario/stock"),
                riesgoSi(!enTransito.isEmpty(), "Traslados sin confirmar",
                        enTransito.size() + " activos siguen en transito.", "alta", "/inventario/traslados"),
                riesgoSi(!enReparacion.isEmpty(), "Reparaciones abiertas",
                        enReparacion.size() + " activos requieren seguimiento tecnico.", "media",
                        "/inventario/reparaciones"))
                .filter(r -> r != null).toList());

        centro.setQuickActions(List.of(
                accion("Recibir OC", "Abrir compras y recepcion por linea.", "/inventario/compras",
                        "feather-shopping-cart", "primary"),
                accion("Asignar activo", "Entregar activos o consumibles.", "/inventario/asignaciones",
                        "feather-user-check", "secondary"),
                accion("Registrar traslado", "Mover activos o stock entre bodegas.", "/inventario/traslados",
                        "feather-repeat", "secondary"),
                accion("Confirmar llegada", "Revisar activos en transito.", "/inventario/traslados",
                        "feather-check-circle", "secondary"),
                accion("Registrar reparacion", "Enviar o retornar activos de reparacion.", "/inventario/reparaciones",
                        "feather-tool", "secondary"),
                accion("Buscar expediente", "Ir al listado de activos.", "/equipos", "feather-search", "secondary")));

        centro.setMovimientosRecientes(safe(() -> inventarioOperacionServicio.buscarMovimientos(0, 10, null, null, null, null)
                .getContent()).stream().limit(10).map(this::movimiento).toList());

        return centro;
    }

    private List<StockConsumibleResponseDTO> stockDisponible() {
        return safe(() -> inventarioOperacionServicio.listarBodegas()).stream()
                .filter(BodegaResponseDTO::isEstado)
                .flatMap(b -> safe(() -> inventarioOperacionServicio.listarStockPorBodega(b.getIdBodega())).stream())
                .toList();
    }

    private long activosSinCustodio(List<EquiposResponseDTO> equipos, List<CustodiasResponseDTO> custodias) {
        Set<Integer> equiposConCustodia = new HashSet<>();
        custodias.stream()
                .filter(CustodiasResponseDTO::isEstado)
                .filter(c -> c.getFkEquipo() != null)
                .forEach(c -> equiposConCustodia.add(c.getFkEquipo().getIdEquipo()));
        return equipos.stream()
                .filter(EquiposResponseDTO::isEstado)
                .filter(e -> !equiposConCustodia.contains(e.getIdEquipo()))
                .count();
    }

    private boolean requiereRecepcion(OrdenCompraResponseDTO orden) {
        if (orden == null) {
            return false;
        }
        String estado = texto(orden.getEstado()).toUpperCase(Locale.ROOT);
        return estado.equals("EMITIDA") || estado.equals("RECEPCION_PARCIAL") || estado.equals("RECIBIDA_PARCIAL");
    }

    private WorkQueueResumenDTO bandeja(String tipo, String titulo, String descripcion, long cantidad, String prioridad,
            String href, String accionTexto, String icono) {
        return new WorkQueueResumenDTO(tipo, titulo, descripcion, cantidad, prioridad, href, accionTexto, icono);
    }

    private RiesgoOperativoDTO riesgoSi(boolean condicion, String titulo, String descripcion, String severidad, String href) {
        return condicion ? new RiesgoOperativoDTO(titulo, descripcion, severidad, href) : null;
    }

    private QuickActionDTO accion(String titulo, String descripcion, String href, String icono, String variante) {
        return new QuickActionDTO(titulo, descripcion, href, icono, variante);
    }

    private MovimientoRecienteDTO movimiento(MovimientoInventarioResponseDTO m) {
        String titulo = texto(m.getTipoMovimiento());
        String item = primero(m.getEquipoCodigo(), m.getConsumibleNombre(), "Item sin codigo");
        String lugar = primero(m.getBodegaDestinoNombre(), m.getBodegaOrigenNombre(), m.getCustodioNombre(), "Sin destino");
        return new MovimientoRecienteDTO(titulo, item, lugar, m.getFechaMovimiento(), m.getCustodioNombre(),
                "/inventario/movimientos");
    }

    private String primero(String... valores) {
        for (String valor : valores) {
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }
        return "";
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private <T> List<T> safe(Source<List<T>> source) {
        try {
            List<T> value = source.get();
            return value == null ? List.of() : value;
        } catch (Exception ignored) {
            return List.of();
        }
    }

    @FunctionalInterface
    private interface Source<T> {
        T get();
    }
}
