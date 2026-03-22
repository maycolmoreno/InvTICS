package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CategoriaEquiposJpa;

public interface ICategoriaEquiposJpaRepositorio extends JpaRepository<CategoriaEquiposJpa, Integer> {

	Optional<CategoriaEquiposJpa> findByNombre(String nombre);

}
