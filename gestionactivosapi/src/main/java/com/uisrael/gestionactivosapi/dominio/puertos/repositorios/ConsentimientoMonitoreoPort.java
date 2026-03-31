package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import java.util.Optional;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ConsentimientoMonitoreoJpa;

public interface ConsentimientoMonitoreoPort {

    ConsentimientoMonitoreoJpa guardar(ConsentimientoMonitoreoJpa consentimiento);

    Optional<ConsentimientoMonitoreoJpa> existeConsentimientoActivo(Integer usuarioId);
}
