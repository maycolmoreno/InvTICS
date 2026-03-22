package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICargosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.DuplicidadException;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Cargos;
import com.uisrael.gestionactivosapi.dominio.repositorios.ICargosRepositorio;

public class CargosUseCaseImpl implements ICargosUseCase {

	private final ICargosRepositorio repositorio;

	public CargosUseCaseImpl(ICargosRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Cargos crear(Cargos cargo) {
		if (repositorio.existeNombre(cargo.getNombre().trim())) {
			throw new DuplicidadException("Ya existe un cargo con ese nombre");
		}
		return repositorio.guardar(cargo);
	}

	@Override
	public Cargos obtenerPorId(int id) {
		return repositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Cargo no encontrado"));
	}

	@Override
	public List<Cargos> listar() {
		return repositorio.listarTodos();
	}

	@Override
	public Cargos actualizar(int id, Cargos cargo) {
		repositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Cargo no encontrado"));

		if (repositorio.existeNombreParaOtro(cargo.getNombre().trim(), id)) {
			throw new DuplicidadException("Ya existe otro cargo con ese nombre");
		}

		Cargos actualizado = new Cargos(id, cargo.getNombre(), cargo.isEstado(),
				cargo.getFkDepartamento());

		return repositorio.actualizar(id, actualizado);
	}

	@Override
	public Cargos actualizarEstado(int id, boolean estado) {
		Cargos actual = repositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Cargo no encontrado"));

		// Solo cambia el estado, mantiene nombre/agencia
		Cargos actualizado = new Cargos(actual.getIdCargo(), // o id, según tu constructor
				actual.getNombre(), estado, actual.getFkDepartamento());

		return repositorio.actualizarEstado(id, actualizado);
	}

	@Override
	public boolean nombreExiste(String nombre) {
		return repositorio.existeNombre(nombre.trim());
	}

	@Override
	public boolean nombreExisteParaOtro(String nombre, Integer idCargo) {
		return repositorio.existeNombreParaOtro(nombre.trim(), idCargo);
	}

}
