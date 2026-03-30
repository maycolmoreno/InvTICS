package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActualizacionActivoJpa;

@Repository
public interface IActualizacionActivoJpaRepositorio extends JpaRepository<ActualizacionActivoJpa, Long> {
}
