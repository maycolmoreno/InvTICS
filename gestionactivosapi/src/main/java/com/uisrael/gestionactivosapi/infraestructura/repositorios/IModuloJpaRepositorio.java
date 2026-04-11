package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ModuloJpa;

public interface IModuloJpaRepositorio extends JpaRepository<ModuloJpa, Integer> {

    List<ModuloJpa> findByEstadoTrueOrderByOrdenAsc();
}
