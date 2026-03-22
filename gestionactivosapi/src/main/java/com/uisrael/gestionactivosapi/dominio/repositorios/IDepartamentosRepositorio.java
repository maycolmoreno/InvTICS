package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;

public interface IDepartamentosRepositorio {

	Departamentos guardar(Departamentos departamento);

	Optional<Departamentos> buscarPorId(int id);

	List<Departamentos> listarTodos();

	Departamentos actualizar(int id, Departamentos departamento);

	Departamentos actualizarEstado(int id, Departamentos departamento);

	boolean existeNombre(String nombre);

	boolean existeNombreParaOtro(String nombre, int idDepartamento);

}
