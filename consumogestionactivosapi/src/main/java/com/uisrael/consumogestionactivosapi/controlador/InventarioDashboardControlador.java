package com.uisrael.consumogestionactivosapi.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional.CentroOperacionalDTO;
import com.uisrael.consumogestionactivosapi.service.ICentroOperacionalServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventario")
public class InventarioDashboardControlador {

    private final ICentroOperacionalServicio centroOperacionalServicio;
    private final IInventarioOperacionServicio inventarioOperacionServicio;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("centro", centroOperacionalServicio.obtenerCentroOperacional());
        return "Inventario/dashboard";
    }

    @GetMapping("/trabajo")
    public String trabajo(Model model) {
        CentroOperacionalDTO centro = centroOperacionalServicio.obtenerCentroOperacional();
        model.addAttribute("centro", centro);
        model.addAttribute("tipoActivo", null);
        return "Inventario/trabajo";
    }

    @GetMapping("/trabajo/{tipo}")
    public String trabajoDetalle(@PathVariable String tipo, Model model) {
        CentroOperacionalDTO centro = centroOperacionalServicio.obtenerCentroOperacional();
        model.addAttribute("centro", centro);
        model.addAttribute("tipoActivo", tipo.toUpperCase(Locale.ROOT));

        String tipoNorm = tipo.toUpperCase(Locale.ROOT);
        switch (tipoNorm) {
            case "RECEPCIONES_PENDIENTES" -> {
                List<OrdenCompraResponseDTO> ocs = safe(() -> inventarioOperacionServicio.listarOrdenesCompra());
                model.addAttribute("filas", ocs.stream()
                        .filter(o -> o != null && esOcPendiente(o.getEstado()))
                        .toList());
            }
            case "TRASLADOS_PENDIENTES" -> model.addAttribute("filas",
                    safe(() -> inventarioOperacionServicio.listarActivosEnTransito()));
            case "REPARACIONES_ABIERTAS" -> model.addAttribute("filas",
                    safe(() -> inventarioOperacionServicio.listarActivosEnReparacion()));
            case "ACTIVOS_SIN_ETIQUETA" -> {
                List<ActivoInventarioResponseDTO> enBodega = safe(() -> inventarioOperacionServicio.listarActivosEnBodega());
                model.addAttribute("filas", enBodega.stream()
                        .filter(a -> Boolean.FALSE.equals(a.getEtiquetado()))
                        .toList());
            }
            default -> model.addAttribute("filas", List.of());
        }
        return "Inventario/trabajo";
    }

    private boolean esOcPendiente(String estado) {
        if (estado == null) return false;
        String e = estado.toUpperCase(Locale.ROOT);
        return e.equals("EMITIDA") || e.equals("RECEPCION_PARCIAL") || e.equals("RECIBIDA_PARCIAL");
    }

    private <T> List<T> safe(java.util.function.Supplier<List<T>> source) {
        try {
            List<T> v = source.get();
            return v != null ? v : List.of();
        } catch (Exception ignored) {
            return List.of();
        }
    }
}
