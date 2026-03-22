package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientoProgramadoJpa;

public interface IMantenimientoProgramadoJpaRepositorio extends JpaRepository<MantenimientoProgramadoJpa, Long> {

    @Override
    @EntityGraph(attributePaths = {"fkEquipo", "fkTecnicoAsignado"})
    List<MantenimientoProgramadoJpa> findAll();

    @EntityGraph(attributePaths = {"fkEquipo", "fkTecnicoAsignado"})
    List<MantenimientoProgramadoJpa> findByEstadoTrue();

    @EntityGraph(attributePaths = {"fkEquipo", "fkTecnicoAsignado"})
    Optional<MantenimientoProgramadoJpa> findByEquipoId(Integer equipoId);

    @EntityGraph(attributePaths = {"fkEquipo", "fkTecnicoAsignado"})
    List<MantenimientoProgramadoJpa> findByFechaProximoMantenimientoLessThanEqualAndEstadoTrue(LocalDate fecha);

    @EntityGraph(attributePaths = {"fkEquipo", "fkTecnicoAsignado"})
    List<MantenimientoProgramadoJpa> findByFechaProximoMantenimientoBetweenAndEstadoTrue(LocalDate inicio, LocalDate fin);
}
