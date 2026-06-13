package com.uisrael.consumogestionactivosapi.controlador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.MovimientoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.StockConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventario")
public class InventarioDashboardControlador {

    private final IEquiposServicio equiposServicio;
    private final ICustodiasServicio custodiasServicio;
    private final IInventarioOperacionServicio inventarioOperacionServicio;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<EquiposResponseDTO> equipos = safeList(equiposServicio.listarEquipos());
        List<CustodiasResponseDTO> custodias = safeList(custodiasServicio.listarCustodias());
        List<OrdenCompraResponseDTO> ordenes = safeList(inventarioOperacionServicio.listarOrdenesCompra());
        List<MovimientoInventarioResponseDTO> movimientos = safeList(inventarioOperacionServicio.listarMovimientosRecientes());
        var bodegas = safeList(inventarioOperacionServicio.listarBodegas());
        List<StockConsumibleResponseDTO> stock = bodegas.isEmpty()
                ? List.of()
                : safeList(inventarioOperacionServicio.listarStockPorBodega(bodegas.get(0).getIdBodega()));

        long totalActivos = equipos.size();
        long custodiasActivas = custodias.stream().filter(c -> c.isEstado() && c.getFkEquipo() != null).count();
        long enBodega = safeList(inventarioOperacionServicio.listarActivosEnBodega()).size();
        long enReparacion = equipos.stream().filter(e -> contiene(e.getEstadoEquipo(), "REPAR")).count();
        long dadosBaja = equipos.stream().filter(e -> contiene(e.getEstadoEquipo(), "BAJA")).count();
        long sinCustodio = Math.max(0, totalActivos - custodiasActivas - dadosBaja);
        long ocPendientes = ordenes.stream().filter(o -> !"RECIBIDA".equalsIgnoreCase(nullToEmpty(o.getEstado()))).count();
        long ocRecibidas = ordenes.stream().filter(o -> "RECIBIDA".equalsIgnoreCase(nullToEmpty(o.getEstado()))).count();

        Map<String, Long> distribucionEstados = new LinkedHashMap<>();
        distribucionEstados.put("Asignados", custodiasActivas);
        distribucionEstados.put("En bodega", enBodega);
        distribucionEstados.put("En reparacion", enReparacion);
        distribucionEstados.put("Dados de baja", dadosBaja);
        distribucionEstados.put("Sin custodio", sinCustodio);

        List<StockConsumibleResponseDTO> stockCritico = stock.stream()
                .filter(s -> s.getCantidad() != null && s.getCantidad() <= 5)
                .sorted(Comparator.comparing(StockConsumibleResponseDTO::getCantidad,
                        Comparator.nullsLast(Integer::compareTo)))
                .limit(5)
                .toList();

        List<OrdenCompraResponseDTO> ordenesPendientes = ordenes.stream()
                .filter(o -> !"RECIBIDA".equalsIgnoreCase(nullToEmpty(o.getEstado())))
                .limit(5)
                .toList();

        model.addAttribute("totalActivos", totalActivos);
        model.addAttribute("activosAsignados", custodiasActivas);
        model.addAttribute("activosEnBodega", enBodega);
        model.addAttribute("activosEnReparacion", enReparacion);
        model.addAttribute("activosDadosBaja", dadosBaja);
        model.addAttribute("equiposSinCustodio", sinCustodio);
        model.addAttribute("ocPendientes", ocPendientes);
        model.addAttribute("ocRecibidas", ocRecibidas);
        model.addAttribute("consumiblesCriticos", stockCritico.size());
        model.addAttribute("proximosMantenimientos", enReparacion);
        model.addAttribute("distribucionEstados", distribucionEstados);
        model.addAttribute("stockCritico", stockCritico);
        model.addAttribute("ordenesPendientes", ordenesPendientes);
        model.addAttribute("movimientos", movimientos.stream().limit(6).toList());
        model.addAttribute("baseDistribucion", Math.max(1, totalActivos));
        return "Inventario/dashboard";
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toUpperCase(Locale.ROOT).contains(texto);
    }

    private String nullToEmpty(String valor) {
        return valor == null ? "" : valor;
    }

    private <T> List<T> safeList(List<T> value) {
        return value == null ? new ArrayList<>() : value;
    }
}
