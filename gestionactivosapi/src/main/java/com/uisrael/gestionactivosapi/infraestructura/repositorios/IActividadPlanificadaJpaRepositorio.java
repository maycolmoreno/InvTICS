package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadPlanificadaJpa;

public interface IActividadPlanificadaJpaRepositorio extends JpaRepository<ActividadPlanificadaJpa, Long> {

    @EntityGraph(attributePaths = {"fkTecnico", "fkCreadoPor", "fkEquipo"})
    List<ActividadPlanificadaJpa> findByTecnicoIdOrderByFechaInicioDesc(Integer tecnicoId);

    @EntityGraph(attributePaths = {"fkTecnico", "fkCreadoPor", "fkEquipo"})
    List<ActividadPlanificadaJpa> findByTecnicoIdAndEstadoOrderByFechaInicioDesc(Integer tecnicoId, String estado);

    @EntityGraph(attributePaths = {"fkTecnico", "fkCreadoPor", "fkEquipo"})
    List<ActividadPlanificadaJpa> findByTecnicoIdAndFechaInicioBetweenOrderByFechaInicioAsc(
            Integer tecnicoId, LocalDate desde, LocalDate hasta);

    @EntityGraph(attributePaths = {"fkTecnico", "fkCreadoPor", "fkEquipo"})
    List<ActividadPlanificadaJpa> findAllByOrderByFechaInicioDesc();

    // Consultas para métricas
    long countByTecnicoIdAndEstado(Integer tecnicoId, String estado);

    long countByTecnicoIdAndFechaInicioBetween(Integer tecnicoId, LocalDate desde, LocalDate hasta);

    long countByTecnicoIdAndEstadoAndFechaInicioBetween(Integer tecnicoId, String estado, LocalDate desde, LocalDate hasta);

    @Query("SELECT COUNT(a) FROM ActividadPlanificadaJpa a WHERE a.tecnicoId = :tecnicoId " +
           "AND a.estado = 'COMPLETADA' AND a.fechaCompletada <= CAST(a.fechaFin AS timestamp)")
    long countCompletadasATiempo(@Param("tecnicoId") Integer tecnicoId);

    @Query("SELECT COUNT(a) FROM ActividadPlanificadaJpa a WHERE a.tecnicoId = :tecnicoId " +
           "AND a.estado = 'COMPLETADA' AND a.fechaCompletada > CAST(a.fechaFin AS timestamp)")
    long countCompletadasTarde(@Param("tecnicoId") Integer tecnicoId);

    @Query("SELECT COALESCE(AVG(a.tiempoRealMinutos), 0) FROM ActividadPlanificadaJpa a " +
           "WHERE a.tecnicoId = :tecnicoId AND a.estado = 'COMPLETADA' AND a.tiempoRealMinutos IS NOT NULL")
    double promedioTiempoRealMinutos(@Param("tecnicoId") Integer tecnicoId);

    // Métricas por rango de fechas
    @Query("SELECT COUNT(a) FROM ActividadPlanificadaJpa a WHERE a.tecnicoId = :tecnicoId " +
           "AND a.estado = 'COMPLETADA' AND a.fechaCompletada <= CAST(a.fechaFin AS timestamp) " +
           "AND a.fechaInicio BETWEEN :desde AND :hasta")
    long countCompletadasATiempoEnPeriodo(@Param("tecnicoId") Integer tecnicoId,
            @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT COUNT(a) FROM ActividadPlanificadaJpa a WHERE a.tecnicoId = :tecnicoId " +
           "AND a.estado = 'VENCIDA' AND a.fechaInicio BETWEEN :desde AND :hasta")
    long countVencidasEnPeriodo(@Param("tecnicoId") Integer tecnicoId,
            @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT COALESCE(AVG(a.tiempoRealMinutos), 0) FROM ActividadPlanificadaJpa a " +
           "WHERE a.tecnicoId = :tecnicoId AND a.estado = 'COMPLETADA' " +
           "AND a.tiempoRealMinutos IS NOT NULL AND a.fechaInicio BETWEEN :desde AND :hasta")
    double promedioTiempoEnPeriodo(@Param("tecnicoId") Integer tecnicoId,
            @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
