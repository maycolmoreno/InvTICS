package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EmpresaJpa;

@Repository
public interface IEmpresaJpaRepositorio extends JpaRepository<EmpresaJpa, Integer> {

    Optional<EmpresaJpa> findByRuc(String ruc);

    List<EmpresaJpa> findByEstadoTrue();

    boolean existsByRucIgnoreCase(String ruc);
}
