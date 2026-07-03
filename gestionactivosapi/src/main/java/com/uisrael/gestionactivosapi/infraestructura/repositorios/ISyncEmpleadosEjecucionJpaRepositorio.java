package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.SyncEmpleadosEjecucionJpa;

public interface ISyncEmpleadosEjecucionJpaRepositorio extends JpaRepository<SyncEmpleadosEjecucionJpa, Integer> {

    Optional<SyncEmpleadosEjecucionJpa> findFirstByOrderByEjecutadoEnDesc();
}
