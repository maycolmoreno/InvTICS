package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRegistrarUbicacionTecnicoUseCase;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ConsentimientoMonitoreoPort;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UbicacionTecnicoPort;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UsuarioRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionTecnicoJpa;

public class RegistrarUbicacionTecnicoUseCaseImpl implements IRegistrarUbicacionTecnicoUseCase {

    private final UbicacionTecnicoPort ubicacionPort;
    private final ConsentimientoMonitoreoPort consentimientoPort;
    private final UsuarioRepositorioPuerto usuarioRepositorio;

    public RegistrarUbicacionTecnicoUseCaseImpl(UbicacionTecnicoPort ubicacionPort,
                                                 ConsentimientoMonitoreoPort consentimientoPort,
                                                 UsuarioRepositorioPuerto usuarioRepositorio) {
        this.ubicacionPort = ubicacionPort;
        this.consentimientoPort = consentimientoPort;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    public UbicacionTecnicoJpa ejecutar(Integer tecnicoId, BigDecimal latitud, BigDecimal longitud,
                                         BigDecimal precisionMetros, LocalDateTime timestampCaptura) {
        // Validar que el usuario existe y tiene rol TECNICO
        Usuarios usuario = usuarioRepositorio.obtenerPorId(tecnicoId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + tecnicoId));

        if (usuario.getFkRol() == null || !"TECNICO".equalsIgnoreCase(usuario.getFkRol().getNombre())) {
            throw new SecurityException("Solo los usuarios con rol TECNICO pueden registrar ubicaciones");
        }

        // Validar que tenga consentimiento activo
        if (consentimientoPort.existeConsentimientoActivo(tecnicoId).isEmpty()) {
            throw new IllegalStateException("El técnico debe aceptar el consentimiento de monitoreo antes de enviar ubicaciones");
        }

        UbicacionTecnicoJpa ubicacion = new UbicacionTecnicoJpa();
        ubicacion.setUsuarioId(tecnicoId);
        ubicacion.setLatitud(latitud);
        ubicacion.setLongitud(longitud);
        ubicacion.setPrecisionMetros(precisionMetros);
        ubicacion.setTimestampCaptura(timestampCaptura != null ? timestampCaptura : LocalDateTime.now());

        return ubicacionPort.guardar(ubicacion);
    }
}
