package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.time.LocalDateTime;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRegistrarConsentimientoUseCase;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ConsentimientoMonitoreoPort;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ConsentimientoMonitoreoJpa;

public class RegistrarConsentimientoUseCaseImpl implements IRegistrarConsentimientoUseCase {

    private final ConsentimientoMonitoreoPort consentimientoPort;

    public RegistrarConsentimientoUseCaseImpl(ConsentimientoMonitoreoPort consentimientoPort) {
        this.consentimientoPort = consentimientoPort;
    }

    @Override
    public ConsentimientoMonitoreoJpa ejecutar(Integer tecnicoId, String versionTerminos, String ipAceptacion) {
        // Validar que no tenga consentimiento activo
        if (consentimientoPort.existeConsentimientoActivo(tecnicoId).isPresent()) {
            throw new IllegalStateException("El técnico ya tiene un consentimiento de monitoreo activo");
        }

        ConsentimientoMonitoreoJpa consentimiento = new ConsentimientoMonitoreoJpa();
        consentimiento.setUsuarioId(tecnicoId);
        consentimiento.setFechaAceptacion(LocalDateTime.now());
        consentimiento.setVersionTerminos(versionTerminos != null ? versionTerminos : "1.0");
        consentimiento.setIpAceptacion(ipAceptacion);
        consentimiento.setActivo(true);

        return consentimientoPort.guardar(consentimiento);
    }
}
