package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionesJpa;

public interface IUbicacionesJpaRepositorio
        extends JpaRepository<UbicacionesJpa, Integer> {

	boolean existsByNombreIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCaseAndIdUbicacionNot(String nombre, Integer idDepartamento);

}
