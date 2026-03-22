package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.DepartamentosJpa;

public interface IDepartamentosJpaRepositorio extends JpaRepository<DepartamentosJpa, Integer> {

	boolean existsByNombreIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCaseAndIdDepartamentoNot(String nombre, Integer idDepartamento);
}
