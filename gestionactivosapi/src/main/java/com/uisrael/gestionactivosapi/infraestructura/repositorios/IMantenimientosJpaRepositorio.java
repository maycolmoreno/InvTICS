package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @EntityGraph(attributePaths = {"fkEquipo", "fkCliente", "fkUsuario"})
    Page<MantenimientosJpa> findAllByOrderByFechaProgramadaDescIdMantenimientoDesc(Pageable pageable);

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

    @EntityGraph(attributePaths = {"fkEquipo", "fkCliente", "fkUsuario"})
    List<MantenimientosJpa> findByIdUsuarioOrderByCreadoEnDesc(Integer idUsuario);

    @EntityGraph(attributePaths = {"fkEquipo", "fkCliente", "fkUsuario"})
    Page<MantenimientosJpa> findByIdUsuarioOrderByFechaProgramadaDescIdMantenimientoDesc(Integer idUsuario, Pageable pageable);

    @Query("SELECT DISTINCT m FROM MantenimientosJpa m LEFT JOIN m.equipos me " +
           "WHERE m.equipoId = :equipoId OR me.equipoId = :equipoId " +
           "ORDER BY m.creadoEn DESC")
    List<MantenimientosJpa> findByEquipoIdIncluyendoMultipleOrderByCreadoEnDesc(@Param("equipoId") Integer equipoId);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END " +
           "FROM MantenimientosJpa m LEFT JOIN m.equipos me " +
           "WHERE (m.equipoId = :equipoId OR me.equipoId = :equipoId) " +
           "AND m.estadoInterno = :estadoInterno")
    boolean existsByEquipoEnProcesoIncluyendoMultiple(
            @Param("equipoId") Integer equipoId,
            @Param("estadoInterno") com.uisrael.gestionactivosapi.dominio.entidades.EstadoInternoMantenimiento estadoInterno);
}
