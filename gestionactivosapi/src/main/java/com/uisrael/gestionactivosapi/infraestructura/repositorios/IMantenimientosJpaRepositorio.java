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

    @Query("select max(m.equipoSnapshot.codigoInternoSnapshot) from MantenimientosJpa m where m.equipoSnapshot.yearSnapshoted = :year")
    String findMaxCodigoInternoSnapshotByYear(@Param("year") Integer year);

    /** @deprecated Usar {@link #findMaxCodigoInternoSnapshotByYear(Integer)} */
    @Deprecated
    default String findMaxSineSnapshotedByYear(Integer year) {
        return findMaxCodigoInternoSnapshotByYear(year);
    }

    Optional<MantenimientosJpa> findTopByEquipoIdAndFecCierreNotNullOrderByFecCierreDesc(Integer equipoId);

    boolean existsByEquipoIdAndEstadoInterno(Integer equipoId,
            com.uisrael.gestionactivosapi.dominio.entidades.EstadoInternoMantenimiento estadoInterno);

    @EntityGraph(attributePaths = {"fkEquipo", "fkCliente", "fkUsuario"})
    List<MantenimientosJpa> findByEquipoIdOrderByCreadoEnDesc(Integer equipoId);

    @EntityGraph(attributePaths = {"fkEquipo", "fkCliente", "fkUsuario"})
    List<MantenimientosJpa> findByIdClienteOrderByCreadoEnDesc(Integer idCliente);
}
