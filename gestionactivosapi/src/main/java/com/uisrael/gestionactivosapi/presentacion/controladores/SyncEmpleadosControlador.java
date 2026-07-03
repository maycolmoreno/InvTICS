package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.servicios.SincronizacionEmpleadosService;
import com.uisrael.gestionactivosapi.presentacion.dto.response.sync.EstadoSincronizacionDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.sync.SincronizacionResultadoDTO;

/**
 * Sincronizacion de empleados. Solo ADMINISTRADOR (ver SecurityConfig).
 */
@RestController
@RequestMapping("/api/sync/empleados")
public class SyncEmpleadosControlador {

    private final SincronizacionEmpleadosService sincronizacionService;

    public SyncEmpleadosControlador(SincronizacionEmpleadosService sincronizacionService) {
        this.sincronizacionService = sincronizacionService;
    }

    /** Sincroniza a partir de un JSON enviado en el cuerpo (subida manual). */
    @PostMapping
    public ResponseEntity<?> sincronizarManual(@RequestBody String json, Authentication authentication) {
        try {
            SincronizacionResultadoDTO resultado = sincronizacionService.sincronizar(
                    sincronizacionService.parsearEmpleadosDesdeJson(json), "MANUAL", nombre(authentication));
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Sincroniza leyendo la fuente configurada (EMPLEADOS_SYNC_URL o EMPLEADOS_SYNC_ARCHIVO). */
    @PostMapping("/desde-fuente")
    public ResponseEntity<?> sincronizarDesdeFuente(Authentication authentication) {
        try {
            return ResponseEntity.ok(sincronizacionService.sincronizarDesdeFuente(nombre(authentication)));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    /** Ultima ejecucion, sus cambios y alertas vigentes. */
    @GetMapping("/estado")
    public EstadoSincronizacionDTO estado() {
        return sincronizacionService.obtenerEstado();
    }

    private static String nombre(Authentication authentication) {
        return authentication != null ? authentication.getName() : "sistema";
    }
}
