package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.dominio.modelo.Pagina;
import com.uisrael.gestionactivosapi.infraestructura.servicios.NotificacionService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.PushNotificacionService;
import com.uisrael.gestionactivosapi.presentacion.dto.response.NotificacionResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.PaginaResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionControlador {

    private final NotificacionService notificacionService;
    private final PushNotificacionService pushNotificacionService;

    @GetMapping
    public List<NotificacionResponseDTO> listar(Principal principal) {
        Integer usuarioId = notificacionService.obtenerUsuarioIdPorCorreo(principal.getName());
        return notificacionService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/paginado")
    public PaginaResponse<NotificacionResponseDTO> listarPaginado(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Integer usuarioId = notificacionService.obtenerUsuarioIdPorCorreo(principal.getName());
        Pagina<NotificacionResponseDTO> pagina = notificacionService.listarPorUsuarioPaginado(usuarioId, page, size);
        PaginaResponse<NotificacionResponseDTO> resp = new PaginaResponse<>();
        resp.setContenido(pagina.contenido());
        resp.setPaginaActual(pagina.paginaActual());
        resp.setTamanioPagina(pagina.tamanioPagina());
        resp.setTotalElementos(pagina.totalElementos());
        resp.setTotalPaginas(pagina.totalPaginas());
        resp.setPrimera(pagina.paginaActual() == 0);
        resp.setUltima(pagina.paginaActual() + 1 >= pagina.totalPaginas());
        return resp;
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

    @PostMapping("/fcm-token")
    public Map<String, String> registrarFcmToken(Principal principal, @RequestBody Map<String, String> body) {
        Integer usuarioId = notificacionService.obtenerUsuarioIdPorCorreo(principal.getName());
        String token = body.get("token");
        if (token != null && !token.isBlank()) {
            pushNotificacionService.registrarToken(usuarioId, token);
            return Map.of("status", "ok");
        }
        return Map.of("status", "error", "mensaje", "Token vacio");
    }

    @PostMapping("/fcm-token/limpiar")
    public Map<String, String> limpiarFcmToken(Principal principal) {
        Integer usuarioId = notificacionService.obtenerUsuarioIdPorCorreo(principal.getName());
        pushNotificacionService.limpiarToken(usuarioId);
        return Map.of("status", "ok");
    }
}
