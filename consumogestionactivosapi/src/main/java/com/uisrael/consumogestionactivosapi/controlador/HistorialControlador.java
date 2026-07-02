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

    // RETIRADO (Fase C2): el historial-360 del vertical paralelo se consolido en la
    // pestana de mantenimientos del expediente del activo. Se redirige para no romper
    // enlaces antiguos.
    @GetMapping("/historial/{equipoId}")
    public String verHistorial(@PathVariable Long equipoId) {
        return "redirect:/activos/equipos/" + equipoId + "/expediente#tab-mantenimientos";
    }
}
