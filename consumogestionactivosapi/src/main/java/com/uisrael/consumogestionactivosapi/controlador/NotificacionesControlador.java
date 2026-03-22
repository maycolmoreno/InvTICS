package com.uisrael.consumogestionactivosapi.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.service.INotificacionServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionesControlador {

    private final INotificacionServicio notificacionServicio;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("notificaciones", notificacionServicio.listar());
        return "mantenimiento/notificaciones";
    }

    @PostMapping("/{id}/leer")
    public String marcarLeida(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notificacionServicio.marcarLeida(id);
        redirectAttributes.addFlashAttribute("exito", "Notificacion marcada como leida");
        return "redirect:/notificaciones";
    }
}
