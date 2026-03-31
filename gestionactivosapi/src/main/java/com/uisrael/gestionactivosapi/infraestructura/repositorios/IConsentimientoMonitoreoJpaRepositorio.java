package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ConsentimientoMonitoreoJpa;

public interface IConsentimientoMonitoreoJpaRepositorio extends JpaRepository<ConsentimientoMonitoreoJpa, Long> {

    Optional<ConsentimientoMonitoreoJpa> findByUsuarioIdAndActivoTrue(Integer usuarioId);
}
