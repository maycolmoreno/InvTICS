package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RolesJpa;

public interface IRolesJpaRepositorio extends JpaRepository<RolesJpa, Integer> {

	Optional<RolesJpa> findByNombre(String nombre);

}
