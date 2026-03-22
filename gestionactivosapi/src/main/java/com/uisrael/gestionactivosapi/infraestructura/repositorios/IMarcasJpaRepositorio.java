package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MarcasJpa;

public interface IMarcasJpaRepositorio extends JpaRepository<MarcasJpa, Integer> {

}
