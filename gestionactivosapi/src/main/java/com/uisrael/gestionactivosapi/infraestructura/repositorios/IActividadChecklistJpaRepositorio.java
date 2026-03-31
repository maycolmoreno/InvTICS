package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadChecklistJpa;

public interface IActividadChecklistJpaRepositorio extends JpaRepository<ActividadChecklistJpa, Integer> {

    List<ActividadChecklistJpa> findAllByEstadoTrueOrderByOrdenAsc();

    @Query("""
            SELECT DISTINCT a
            FROM ActividadChecklistJpa a
            JOIN a.categorias c
            WHERE a.estado = true AND c.idCategoria = :idCategoria
            ORDER BY a.orden ASC
            """)
    List<ActividadChecklistJpa> findActivasPorCategoria(@Param("idCategoria") Integer idCategoria);
}
