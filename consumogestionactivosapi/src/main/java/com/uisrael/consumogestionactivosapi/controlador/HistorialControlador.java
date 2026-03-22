package com.uisrael.consumogestionactivosapi.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.HistorialCompletoDTO;
import com.uisrael.consumogestionactivosapi.service.IHistorialEquipoServicio;

@Controller
public class HistorialControlador {

    private final IHistorialEquipoServicio historialServicio;
    private static final String TAB_HISTORIAL = "historial";
    private static final String TAB_INFO = "info";
    private static final String TAB_STATS = "stats";

    public HistorialControlador(IHistorialEquipoServicio historialServicio) {
        this.historialServicio = historialServicio;
    }

    @GetMapping("/historial/{equipoId}")
    public String verHistorial(@PathVariable Long equipoId,
            @RequestParam(defaultValue = "historial") String tab,
            Model model) {
        String tabNormalizado = normalizarTab(tab);
        HistorialCompletoDTO historial = historialServicio.obtenerHistorial(equipoId);
        model.addAttribute("historial", historial);
        model.addAttribute("tab", tabNormalizado);
        return "Historial/historial-equipo";
    }

    private String normalizarTab(String tab) {
        if (TAB_INFO.equalsIgnoreCase(tab)) {
            return TAB_INFO;
        }
        if (TAB_STATS.equalsIgnoreCase(tab)) {
            return TAB_STATS;
        }
        return TAB_HISTORIAL;
    }
}
