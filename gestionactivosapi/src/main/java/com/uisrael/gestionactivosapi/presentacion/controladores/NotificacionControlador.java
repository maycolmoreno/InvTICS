package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.servicios.NotificacionService;
import com.uisrael.gestionactivosapi.presentacion.dto.response.NotificacionResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionControlador {

    private final NotificacionService notificacionService;

    @GetMapping
    public List<NotificacionResponseDTO> listar(Principal principal) {
        Integer usuarioId = notificacionService.obtenerUsuarioIdPorCorreo(principal.getName());
        return notificacionService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/count")
    public Map<String, Long> contarNoLeidas(Principal principal) {
        Integer usuarioId = notificacionService.obtenerUsuarioIdPorCorreo(principal.getName());
        return Map.of("count", notificacionService.contarNoLeidas(usuarioId));
    }

    @PostMapping("/{id}/leer")
    public void marcarLeida(@PathVariable Long id) {
        notificacionService.marcarLeida(id);
    }
}
