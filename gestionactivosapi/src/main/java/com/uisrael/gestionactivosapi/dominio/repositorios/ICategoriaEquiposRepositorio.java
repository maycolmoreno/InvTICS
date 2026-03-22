package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.CategoriaEquipos;

public interface ICategoriaEquiposRepositorio {

	CategoriaEquipos guardar(CategoriaEquipos categoriaEquipo);

	Optional<CategoriaEquipos> buscarPorId(int id);

	List<CategoriaEquipos> listarTodos();

	void eliminar(int id);

	Optional<CategoriaEquipos> buscarPorNombre(String nombre);
}
