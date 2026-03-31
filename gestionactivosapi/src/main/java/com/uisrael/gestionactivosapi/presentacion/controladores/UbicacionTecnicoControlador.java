package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IConsultarUbicacionesTiempoRealUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRegistrarConsentimientoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRegistrarUbicacionTecnicoUseCase;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionTecnicoJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.request.ConsentimientoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.UbicacionTecnicoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.UbicacionActivaDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ubicaciones-tecnicos")
public class UbicacionTecnicoControlador {

    private final IRegistrarUbicacionTecnicoUseCase registrarUbicacionUseCase;
    private final IRegistrarConsentimientoUseCase registrarConsentimientoUseCase;
    private final IConsultarUbicacionesTiempoRealUseCase consultarUbicacionesUseCase;
    private final IUsuariosJpaRepositorio usuariosJpaRepositorio;

    public UbicacionTecnicoControlador(IRegistrarUbicacionTecnicoUseCase registrarUbicacionUseCase,
                                        IRegistrarConsentimientoUseCase registrarConsentimientoUseCase,
                                        IConsultarUbicacionesTiempoRealUseCase consultarUbicacionesUseCase,
                                        IUsuariosJpaRepositorio usuariosJpaRepositorio) {
        this.registrarUbicacionUseCase = registrarUbicacionUseCase;
        this.registrarConsentimientoUseCase = registrarConsentimientoUseCase;
        this.consultarUbicacionesUseCase = consultarUbicacionesUseCase;
        this.usuariosJpaRepositorio = usuariosJpaRepositorio;
    }

    @PostMapping
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<?> registrarUbicacion(@Valid @RequestBody UbicacionTecnicoRequestDTO request,
                                                 Principal principal) {
        // Validar que el técnico solo envíe sus propias coordenadas
        Integer usuarioAutenticadoId = obtenerUsuarioIdDesdeCorreo(principal.getName());
        if (!usuarioAutenticadoId.equals(request.getTecnicoId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo puede registrar coordenadas para su propio usuario");
        }

        var ubicacion = registrarUbicacionUseCase.ejecutar(
                request.getTecnicoId(),
                request.getLatitud(),
                request.getLongitud(),
                request.getPrecisionMetros(),
                request.getTimestampCaptura()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(ubicacion);
    }

    @PostMapping("/consentimiento")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<?> registrarConsentimiento(@Valid @RequestBody ConsentimientoRequestDTO request,
                                                      Principal principal) {
        // Validar que el técnico solo registre su propio consentimiento
        Integer usuarioAutenticadoId = obtenerUsuarioIdDesdeCorreo(principal.getName());
        if (!usuarioAutenticadoId.equals(request.getTecnicoId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo puede registrar consentimiento para su propio usuario");
        }

        var consentimiento = registrarConsentimientoUseCase.ejecutar(
                request.getTecnicoId(),
                request.getVersionTerminos(),
                request.getIpAceptacion()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(consentimiento);
    }

    @GetMapping("/tiempo-real")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UbicacionActivaDTO>> consultarTiempoReal() {
        List<UbicacionTecnicoJpa> ubicaciones = consultarUbicacionesUseCase.ejecutar();

        List<UbicacionActivaDTO> resultado = ubicaciones.stream()
                .map(u -> new UbicacionActivaDTO(
                        u.getUsuarioId(),
                        u.getUsuario() != null ? u.getUsuario().getNombre() : null,
                        u.getUsuario() != null && u.getUsuario().getFkDepartamento() != null
                                ? u.getUsuario().getFkDepartamento().getNombre() : null,
                        u.getLatitud(),
                        u.getLongitud(),
                        u.getPrecisionMetros(),
                        Duration.between(u.getTimestampCaptura(), LocalDateTime.now()).toMinutes()
                ))
                .toList();

        return ResponseEntity.ok(resultado);
    }

    private Integer obtenerUsuarioIdDesdeCorreo(String correo) {
        UsuariosJpa usuario = usuariosJpaRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return usuario.getIdUsuario();
    }
}
