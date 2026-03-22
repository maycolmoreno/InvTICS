package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;

public interface ICustodiosJpaRepositorio extends JpaRepository<CustodiosJpa, Integer> {

	boolean existsByCedulaIgnoreCase(String cedula);

	boolean existsByCedulaIgnoreCaseAndIdCustodioNot(String cedula, Integer idCustodio);

	boolean existsByCorreoIgnoreCase(String correo);

	boolean existsByCorreoIgnoreCaseAndIdCustodioNot(String correo, Integer idCustodio);

	boolean existsByFkUsuario_IdUsuario(Integer idUsuario);

	boolean existsByFkUsuario_IdUsuarioAndIdCustodioNot(Integer idUsuario, Integer idCustodio);

}
