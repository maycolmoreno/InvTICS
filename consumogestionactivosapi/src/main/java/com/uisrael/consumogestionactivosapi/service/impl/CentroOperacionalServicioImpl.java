package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
import com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional.CustodioTopDTO;
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
        List<ActivoInventarioResponseDTO> sinInventario = safe(() -> inventarioOperacionServicio.listarSinInventario());
        List<StockConsumibleResponseDTO> stock = stockDisponible();
        List<EquiposResponseDTO> equipos = safe(() -> equiposServicio.listarEquipos());
        List<CustodiasResponseDTO> custodias = safe(() -> custodiasServicio.listarCustodias());

        long recepcionesPendientes = ordenes.stream().filter(this::requiereRecepcion).count();
        long stockCritico = stock.stream()
                .filter(s -> s.getCantidad() != null && s.getCantidad() <= STOCK_CRITICO)
                .count();
        long activosSinEtiqueta = enBodega.stream().filter(a -> Boolean.FALSE.equals(a.getEtiquetado())).count();
        Set<Integer> equiposConCustodia = equiposConCustodiaActiva(custodias);
        long activosSinCustodio = equipos.stream()
                .filter(EquiposResponseDTO::isEstado)
                .filter(e -> !equiposConCustodia.contains(e.getIdEquipo()))
                .count();
        long totalSinInventario = sinInventario.size();

        centro.setBandejas(List.of(
                bandeja("ACTIVOS_SIN_INVENTARIO", "Activos sin inventario", "Equipos existentes sin estado de inventario asignado.",
                        totalSinInventario, "alta", "/inventario/inventario-inicial", "Adoptar", "feather-inbox"),
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
                        enReparacion.size(), "media", "/inventario/reparaciones", "Dar seguimiento", "feather-tool")));

        centro.setRiesgos(Stream.of(
                riesgoSi(totalSinInventario > 0, "Activos sin inventario",
                        totalSinInventario + " equipos no tienen estado de inventario. Deben adoptarse antes de asignarse.", "alta",
                        "/inventario/inventario-inicial"),
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
                accion("Asignar activo", "Asignar activos en bodega a custodios.", "/inventario/asignaciones",
                        "feather-user-check", "secondary"),
                accion("Entregar consumible", "Entregar consumibles desde stock por bodega.", "/inventario/stock",
                        "feather-archive", "secondary"),
                accion("Registrar traslado", "Mover activos o stock entre bodegas.", "/inventario/traslados",
                        "feather-repeat", "secondary"),
                accion("Confirmar llegada", "Revisar activos en transito.", "/inventario/traslados",
                        "feather-check-circle", "secondary"),
                accion("Registrar reparacion", "Enviar o retornar activos de reparacion.", "/inventario/reparaciones",
                        "feather-tool", "secondary")));

        centro.setMovimientosRecientes(safe(() -> inventarioOperacionServicio.buscarMovimientos(0, 10, null, null, null, null)
                .getContent()).stream().limit(10).map(this::movimiento).toList());

        // KPIs
        long totalActivos = equipos.stream().filter(EquiposResponseDTO::isEstado).count();
        long activosAsignados = (long) equiposConCustodia.size();
        centro.setTotalActivos(totalActivos);
        centro.setActivosAsignados(activosAsignados);
        centro.setActivosEnBodega(enBodega.size());
        centro.setActivosEnReparacion(enReparacion.size());
        centro.setActivosEnTransito(enTransito.size());

        // Activos por categoría (top 6 + Otros)
        Map<String, Long> porCategoria = equipos.stream()
                .filter(EquiposResponseDTO::isEstado)
                .filter(e -> e.getFkCategoria() != null && e.getFkCategoria().getNombre() != null)
                .collect(Collectors.groupingBy(e -> e.getFkCategoria().getNombre(), Collectors.counting()));
        List<Map.Entry<String, Long>> categoriasOrdenadas = new ArrayList<>(porCategoria.entrySet());
        categoriasOrdenadas.sort(Map.Entry.<String, Long>comparingByValue().reversed());
        Map<String, Long> categoriaFinal = new LinkedHashMap<>();
        long otros = 0;
        for (int i = 0; i < categoriasOrdenadas.size(); i++) {
            if (i < 5) {
                categoriaFinal.put(categoriasOrdenadas.get(i).getKey(), categoriasOrdenadas.get(i).getValue());
            } else {
                otros += categoriasOrdenadas.get(i).getValue();
            }
        }
        if (otros > 0) categoriaFinal.put("Otros", otros);
        centro.setActivosPorCategoria(categoriaFinal);

        // Top 5 custodios por cantidad de activos asignados
        Map<String, Long> custodioCount = custodias.stream()
                .filter(CustodiasResponseDTO::isEstado)
                .filter(c -> c.getFkCustodio() != null && c.getFkCustodio().getNombre() != null)
                .collect(Collectors.groupingBy(c -> c.getFkCustodio().getNombre(), Collectors.counting()));
        List<CustodioTopDTO> top5 = custodioCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new CustodioTopDTO(e.getKey(), null, e.getValue()))
                .collect(Collectors.toList());
        centro.setTop5Custodios(top5);

        // Activos por ubicación (bodegaNombre como proxy)
        Map<String, Long> porUbicacion = enBodega.stream()
                .filter(a -> a.getBodegaNombre() != null)
                .collect(Collectors.groupingBy(ActivoInventarioResponseDTO::getBodegaNombre, Collectors.counting()));
        List<Map.Entry<String, Long>> ubicOrdenadas = new ArrayList<>(porUbicacion.entrySet());
        ubicOrdenadas.sort(Map.Entry.<String, Long>comparingByValue().reversed());
        Map<String, Long> ubicFinal = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : ubicOrdenadas) {
            ubicFinal.put(entry.getKey(), entry.getValue());
        }
        centro.setActivosPorUbicacion(ubicFinal);

        return centro;
    }

    private List<StockConsumibleResponseDTO> stockDisponible() {
        return safe(() -> inventarioOperacionServicio.listarBodegas()).stream()
                .filter(BodegaResponseDTO::isEstado)
                .flatMap(b -> safe(() -> inventarioOperacionServicio.listarStockPorBodega(b.getIdBodega())).stream())
                .toList();
    }

    private Set<Integer> equiposConCustodiaActiva(List<CustodiasResponseDTO> custodias) {
        Set<Integer> set = new HashSet<>();
        custodias.stream()
                .filter(CustodiasResponseDTO::isEstado)
                .filter(c -> c.getFkEquipo() != null)
                .forEach(c -> set.add(c.getFkEquipo().getIdEquipo()));
        return set;
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
                "/inventario/dashboard");
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
