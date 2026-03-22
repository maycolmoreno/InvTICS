package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Cargos;


public interface ICargosRepositorio {

	Cargos guardar(Cargos cargo);

	Optional<Cargos> buscarPorId(int id);

	List<Cargos> listarTodos();

	Cargos actualizar(int id, Cargos cargo);

	Cargos actualizarEstado(int id, Cargos cargo);

	boolean existeNombre(String nombre);

	boolean existeNombreParaOtro(String nombre, int idCargo);
}
