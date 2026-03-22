package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CargosJpa;

public interface ICargosJpaRepositorio extends JpaRepository<CargosJpa, Integer>{

	boolean existsByNombreIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCaseAndIdCargoNot(String nombre, Integer idCargo);

}
