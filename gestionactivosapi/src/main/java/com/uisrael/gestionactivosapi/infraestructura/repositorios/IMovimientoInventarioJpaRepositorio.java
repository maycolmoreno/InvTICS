package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MovimientoInventarioJpa;

public interface IMovimientoInventarioJpaRepositorio
        extends JpaRepository<MovimientoInventarioJpa, Integer>,
                JpaSpecificationExecutor<MovimientoInventarioJpa> {

    @EntityGraph(attributePaths = {"equipo", "consumible", "bodegaOrigen", "bodegaDestino", "custodio"})
    List<MovimientoInventarioJpa> findTop100ByOrderByFechaMovimientoDesc();

    List<MovimientoInventarioJpa> findByEquipo_IdEquipoOrderByFechaMovimientoDesc(Integer idEquipo);
    List<MovimientoInventarioJpa> findByBodegaDestino_IdBodegaOrderByFechaMovimientoDesc(Integer idBodega);

    Optional<MovimientoInventarioJpa> findFirstByEquipo_IdEquipoAndEstadoNuevoOrderByFechaMovimientoDesc(
            Integer idEquipo, String estadoNuevo);
}
