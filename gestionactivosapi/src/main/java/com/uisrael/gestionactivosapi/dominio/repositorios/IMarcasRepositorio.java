package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;

public interface IMarcasRepositorio {

	Marcas guardar(Marcas Marcas);

	Optional <Marcas> buscarPorId(int id);

	List<Marcas> listarTodos();

	Marcas actualizar(int id, Marcas marcas);

	void eliminar(int id);

}
