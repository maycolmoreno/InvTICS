package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientosJpa;

public interface IMantenimientosJpaRepositorio extends JpaRepository<MantenimientosJpa, Integer> {

    @Override
    @EntityGraph(attributePaths = {"fkEquipo", "fkCliente", "fkUsuario"})
    Optional<MantenimientosJpa> findById(Integer id);

    @EntityGraph(attributePaths = {"fkEquipo", "fkCliente", "fkUsuario"})
    List<MantenimientosJpa> findAllByOrderByFechaProgramadaDescIdMantenimientoDesc();

    @Query("select max(m.equipoSnapshot.sineSnapshot) from MantenimientosJpa m where m.equipoSnapshot.yearSnapshoted = :year")
    String findMaxSineSnapshotedByYear(@Param("year") Integer year);

    Optional<MantenimientosJpa> findTopByEquipoIdAndFecCierreNotNullOrderByFecCierreDesc(Integer equipoId);

    @EntityGraph(attributePaths = {"fkEquipo", "fkCliente", "fkUsuario"})
    List<MantenimientosJpa> findByEquipoIdOrderByCreadoEnDesc(Integer equipoId);

    @EntityGraph(attributePaths = {"fkEquipo", "fkCliente", "fkUsuario"})
    List<MantenimientosJpa> findByIdClienteOrderByCreadoEnDesc(Integer idCliente);
}
