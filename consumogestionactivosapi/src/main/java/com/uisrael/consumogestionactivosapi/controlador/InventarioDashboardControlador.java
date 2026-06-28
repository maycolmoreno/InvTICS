package com.uisrael.consumogestionactivosapi.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.consumogestionactivosapi.service.ICentroOperacionalServicio;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventario")
public class InventarioDashboardControlador {

    private final ICentroOperacionalServicio centroOperacionalServicio;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("centro", centroOperacionalServicio.obtenerCentroOperacional());
        return "Inventario/dashboard";
    }
}
