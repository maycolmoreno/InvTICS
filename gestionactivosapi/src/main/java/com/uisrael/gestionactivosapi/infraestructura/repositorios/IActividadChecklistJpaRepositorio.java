package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadChecklistJpa;

public interface IActividadChecklistJpaRepositorio extends JpaRepository<ActividadChecklistJpa, Integer> {

    List<ActividadChecklistJpa> findAllByEstadoTrueOrderByCategoriaAscOrdenAsc();

    @Query("""
            select a
            from ActividadChecklistJpa a
            join ChecklistCategoriaJpa c on c.idActividad = a.idActividad
            where a.estado = true and c.idCategoria = :idCategoria
            order by a.categoria asc, a.orden asc
            """)
    List<ActividadChecklistJpa> findActivasPorCategoria(@Param("idCategoria") Integer idCategoria);
}
