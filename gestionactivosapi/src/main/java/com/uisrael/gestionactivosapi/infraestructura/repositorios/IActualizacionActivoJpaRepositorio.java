package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActualizacionActivoJpa;

@Repository
public interface IActualizacionActivoJpaRepositorio extends JpaRepository<ActualizacionActivoJpa, Long> {

    @Query("SELECT a FROM ActualizacionActivoJpa a LEFT JOIN FETCH a.usuarioRel WHERE a.activoId = :activoId ORDER BY a.fechaActualizacion DESC")
    List<ActualizacionActivoJpa> findByActivoIdConUsuario(@Param("activoId") Integer activoId);
}
