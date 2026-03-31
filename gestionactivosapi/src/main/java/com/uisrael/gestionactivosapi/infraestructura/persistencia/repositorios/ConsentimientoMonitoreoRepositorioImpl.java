package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ConsentimientoMonitoreoPort;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ConsentimientoMonitoreoJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IConsentimientoMonitoreoJpaRepositorio;

public class ConsentimientoMonitoreoRepositorioImpl implements ConsentimientoMonitoreoPort {

    private final IConsentimientoMonitoreoJpaRepositorio jpaRepositorio;

    public ConsentimientoMonitoreoRepositorioImpl(IConsentimientoMonitoreoJpaRepositorio jpaRepositorio) {
        this.jpaRepositorio = jpaRepositorio;
    }

    @Override
    public ConsentimientoMonitoreoJpa guardar(ConsentimientoMonitoreoJpa consentimiento) {
        return jpaRepositorio.save(consentimiento);
    }

    @Override
    public Optional<ConsentimientoMonitoreoJpa> existeConsentimientoActivo(Integer usuarioId) {
        return jpaRepositorio.findByUsuarioIdAndActivoTrue(usuarioId);
    }
}
