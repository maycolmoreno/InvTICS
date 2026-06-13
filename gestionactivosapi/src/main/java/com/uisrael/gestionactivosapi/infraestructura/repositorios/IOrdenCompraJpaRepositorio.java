package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraJpa;

public interface IOrdenCompraJpaRepositorio extends JpaRepository<OrdenCompraJpa, Integer> {
    Optional<OrdenCompraJpa> findByNumeroOcIgnoreCase(String numeroOc);
    boolean existsByNumeroOcIgnoreCase(String numeroOc);
}
