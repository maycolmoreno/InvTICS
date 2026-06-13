package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MovimientoInventarioJpa;

public interface IMovimientoInventarioJpaRepositorio extends JpaRepository<MovimientoInventarioJpa, Integer> {
    @EntityGraph(attributePaths = {"equipo", "consumible", "bodegaOrigen", "bodegaDestino", "custodio"})
    List<MovimientoInventarioJpa> findTop100ByOrderByFechaMovimientoDesc();

    List<MovimientoInventarioJpa> findByEquipo_IdEquipoOrderByFechaMovimientoDesc(Integer idEquipo);
    List<MovimientoInventarioJpa> findByBodegaDestino_IdBodegaOrderByFechaMovimientoDesc(Integer idBodega);
}
