package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Roles;

public interface IRolesRepositorio {

	Roles guardar(Roles rol);

	Optional<Roles> buscarPorId(int id);

	List<Roles> listarTodos();

	void eliminar(int id);

	Optional<Roles> buscarPorNombre(String nombre);

}
