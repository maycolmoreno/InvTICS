package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;

public interface IUsuariosRepositorio {

	Usuarios guardar(Usuarios usuario);

	Optional<Usuarios> buscarPorId(int id);

	List<Usuarios> listarTodos();

	void eliminar(int id);

	Optional<Usuarios> buscarPorCorreo(String correo);

}
