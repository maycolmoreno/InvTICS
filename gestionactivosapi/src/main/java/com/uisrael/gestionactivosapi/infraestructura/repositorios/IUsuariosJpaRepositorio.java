package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;

public interface IUsuariosJpaRepositorio extends JpaRepository<UsuariosJpa, Integer> {

	@EntityGraph(attributePaths = {"fkDepartamento", "fkDepartamento.fkUbicacion", "fkRol"})
	Optional<UsuariosJpa> findByCorreo(String correo);

}
