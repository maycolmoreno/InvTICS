package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.DepartamentosJpa;

public interface IDepartamentosJpaRepositorio extends JpaRepository<DepartamentosJpa, Integer> {

	boolean existsByNombreIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCaseAndIdDepartamentoNot(String nombre, Integer idDepartamento);

	Optional<DepartamentosJpa> findByNombreIgnoreCase(String nombre);

	List<DepartamentosJpa> findAllByEstadoTrue();
}
