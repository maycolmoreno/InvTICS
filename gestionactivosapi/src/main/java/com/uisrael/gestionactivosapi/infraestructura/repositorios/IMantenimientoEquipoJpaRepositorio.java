package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.gestionactivosapi.dominio.entidades.EstadoInternoMantenimiento;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientoEquipoJpa;

public interface IMantenimientoEquipoJpaRepositorio extends JpaRepository<MantenimientoEquipoJpa, Integer> {

    @EntityGraph(attributePaths = {"equipo"})
    List<MantenimientoEquipoJpa> findByMantenimientoId(Integer mantenimientoId);

    @Query("SELECT CASE WHEN COUNT(me) > 0 THEN TRUE ELSE FALSE END " +
           "FROM MantenimientoEquipoJpa me " +
           "WHERE me.equipoId = :equipoId " +
           "AND me.mantenimiento.estadoInterno = :estadoInterno")
    boolean existsByEquipoIdYEstadoInterno(
            @Param("equipoId") Integer equipoId,
            @Param("estadoInterno") EstadoInternoMantenimiento estadoInterno);
}
