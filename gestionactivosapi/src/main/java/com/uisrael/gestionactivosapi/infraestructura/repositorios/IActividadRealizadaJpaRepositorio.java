package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadRealizadaJpa;

public interface IActividadRealizadaJpaRepositorio extends JpaRepository<ActividadRealizadaJpa, Integer> {

    void deleteByIdMantenimiento(Integer idMantenimiento);

    List<ActividadRealizadaJpa> findAllByIdMantenimiento(Integer idMantenimiento);
}
