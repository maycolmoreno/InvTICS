package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.BodegaJpa;

public interface IBodegaJpaRepositorio extends JpaRepository<BodegaJpa, Integer> {
    Optional<BodegaJpa> findByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCase(String codigo);
}
