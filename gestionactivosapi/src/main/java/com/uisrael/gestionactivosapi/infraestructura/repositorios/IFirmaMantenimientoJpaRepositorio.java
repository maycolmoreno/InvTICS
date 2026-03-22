package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.dominio.entidades.TipoFirma;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.FirmaMantenimientoJpa;

public interface IFirmaMantenimientoJpaRepositorio extends JpaRepository<FirmaMantenimientoJpa, Integer> {

    List<FirmaMantenimientoJpa> findByIdMantenimiento(Integer idMantenimiento);

    Optional<FirmaMantenimientoJpa> findByIdMantenimientoAndTipoFirma(Integer idMantenimiento, TipoFirma tipoFirma);
}
