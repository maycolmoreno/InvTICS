package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.SyncEmpleadosCambioJpa;

public interface ISyncEmpleadosCambioJpaRepositorio extends JpaRepository<SyncEmpleadosCambioJpa, Integer> {

    List<SyncEmpleadosCambioJpa> findByEjecucion_IdEjecucionOrderByIdCambioAsc(Integer idEjecucion);
}
