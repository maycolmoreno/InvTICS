package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ImagenMantenimientoJpa;

public interface IImagenMantenimientoJpaRepositorio extends JpaRepository<ImagenMantenimientoJpa, Long> {

    List<ImagenMantenimientoJpa> findByIdMantenimiento(Integer idMantenimiento);
}
