package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ConsumibleJpa;

public interface IConsumibleJpaRepositorio extends JpaRepository<ConsumibleJpa, Integer> {
    Optional<ConsumibleJpa> findByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCase(String codigo);
}
