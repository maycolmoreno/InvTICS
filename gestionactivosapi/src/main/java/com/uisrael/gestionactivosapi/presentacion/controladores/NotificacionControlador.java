package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.dominio.entidades.EstadoInternoMantenimiento;
import com.uisrael.gestionactivosapi.dominio.modelo.Pagina;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientoProgramadoJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientoProgramadoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;
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
    private final IUsuariosJpaRepositorio usuariosRepo;
    private final IMantenimientosJpaRepositorio mantenimientosRepo;
    private final IMantenimientoProgramadoJpaRepositorio programadoRepo;

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

    /**
     * Endpoint de prueba: envía alertas reales con información de mantenimientos
     * pendientes y programados a todos los técnicos activos.
     * Solo accesible por ADMINISTRADOR.
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/test/tecnicos")
    public Map<String, Object> enviarNotificacionPruebaTecnicos() {
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<UsuariosJpa> tecnicos = usuariosRepo.findAllByFkRol_NombreAndEstadoTrue("TECNICO");

        List<Map<String, Object>> resultados = new ArrayList<>();
        int enviados = 0;
        int sinToken = 0;
        int fallidos = 0;
        int notificacionesCreadas = 0;

        for (UsuariosJpa tecnico : tecnicos) {
            Integer uid = tecnico.getIdUsuario();
            Map<String, Object> detalle = new LinkedHashMap<>();
            detalle.put("id", uid);
            detalle.put("nombre", tecnico.getNombre());
            detalle.put("correo", tecnico.getCorreo());
            detalle.put("tieneToken", tecnico.getFcmToken() != null && !tecnico.getFcmToken().isBlank());

            // --- Datos reales del técnico ---
            long pendientes = mantenimientosRepo.countByIdUsuarioAndEstadoInterno(uid, EstadoInternoMantenimiento.PENDIENTE);
            long enProceso = mantenimientosRepo.countByIdUsuarioAndEstadoInterno(uid, EstadoInternoMantenimiento.EN_PROCESO);
            long totalProgramados = programadoRepo.countByTecnicoIdAndEstadoTrue(uid);
            List<MantenimientoProgramadoJpa> vencidos = programadoRepo
                    .findByTecnicoIdAndFechaProximoMantenimientoLessThanEqualAndEstadoTrue(uid, hoy);

            detalle.put("mantenimientosPendientes", pendientes);
            detalle.put("mantenimientosEnProceso", enProceso);
            detalle.put("programadosActivos", totalProgramados);
            detalle.put("programadosVencidos", vencidos.size());

            // --- Notificación 1: Resumen de actividad ---
            String resumen = String.format(
                    "%s, tienes %d mantenimiento(s) pendiente(s) y %d en proceso. Revisa tu bandeja.",
                    tecnico.getNombre(), pendientes, enProceso);
            notificacionService.crear(uid, resumen, "/mantenimiento", null);
            notificacionesCreadas++;

            // --- Notificación 2: Mantenimientos programados vencidos ---
            if (!vencidos.isEmpty()) {
                for (MantenimientoProgramadoJpa mp : vencidos) {
                    String equipo = mp.getFkEquipo() != null ? mp.getFkEquipo().getCodigoSap() : "Equipo #" + mp.getEquipoId();
                    String fechaVenc = mp.getFechaProximoMantenimiento().format(fmt);
                    String msgVencido = String.format(
                            "Mantenimiento programado VENCIDO: %s - Fecha límite: %s. Requiere atención inmediata.",
                            equipo, fechaVenc);
                    notificacionService.crear(uid, msgVencido, "/mantenimiento/programado", null);
                    notificacionesCreadas++;
                }
            } else if (totalProgramados > 0) {
                String msgProg = String.format(
                        "Tienes %d equipo(s) con mantenimiento programado activo. Verifica las fechas próximas.",
                        totalProgramados);
                notificacionService.crear(uid, msgProg, "/mantenimiento/programado", null);
                notificacionesCreadas++;
            }

            // --- Push notification con resumen ---
            if (tecnico.getFcmToken() != null && !tecnico.getFcmToken().isBlank()) {
                String pushMsg = String.format("Pendientes: %d | En proceso: %d | Programados vencidos: %d",
                        pendientes, enProceso, vencidos.size());
                boolean ok = pushNotificacionService.enviar(uid, "CRESIO - Resumen de actividad", pushMsg, "/mantenimiento");
                detalle.put("pushEnviado", ok);
                if (ok) enviados++; else fallidos++;
            } else {
                detalle.put("pushEnviado", false);
                sinToken++;
            }
            resultados.add(detalle);
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("firebaseDisponible", pushNotificacionService.isDisponible());
        respuesta.put("totalTecnicos", tecnicos.size());
        respuesta.put("notificacionesCreadas", notificacionesCreadas);
        respuesta.put("pushEnviados", enviados);
        respuesta.put("sinTokenFCM", sinToken);
        respuesta.put("fallidos", fallidos);
        respuesta.put("detalle", resultados);
        return respuesta;
    }
}
