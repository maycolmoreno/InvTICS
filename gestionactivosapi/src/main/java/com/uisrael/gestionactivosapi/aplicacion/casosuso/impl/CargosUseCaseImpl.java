package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICargosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.DuplicidadException;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Cargos;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CargosRepositorioPuerto;

public class CargosUseCaseImpl implements ICargosUseCase {

	private final CargosRepositorioPuerto cargosRepositorio;

	public CargosUseCaseImpl(CargosRepositorioPuerto cargosRepositorio) {
		this.cargosRepositorio = cargosRepositorio;
	}

	@Override
	public Cargos crear(Cargos cargo) {
		if (cargosRepositorio.existeNombre(cargo.getNombre().trim())) {
			throw new DuplicidadException("Ya existe un cargo con ese nombre");
		}
		return cargosRepositorio.guardar(cargo);
	}

	@Override
	public Cargos obtenerPorId(int id) {
		return cargosRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Cargo no encontrado"));
	}

	@Override
	public List<Cargos> listar() {
		return cargosRepositorio.listarTodos();
	}

	@Override
	public Cargos actualizar(int id, Cargos cargo) {
		cargosRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Cargo no encontrado"));

		if (cargosRepositorio.existeNombreParaOtro(cargo.getNombre().trim(), id)) {
			throw new DuplicidadException("Ya existe otro cargo con ese nombre");
		}

		Cargos actualizado = new Cargos(id, cargo.getNombre(), cargo.isEstado(),
				cargo.getFkDepartamento());

		return cargosRepositorio.actualizar(id, actualizado);
	}

	@Override
	public Cargos actualizarEstado(int id, boolean estado) {
		Cargos actual = cargosRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Cargo no encontrado"));

		// Solo cambia el estado, mantiene nombre/agencia
		Cargos actualizado = new Cargos(actual.getIdCargo(), // o id, según tu constructor
				actual.getNombre(), estado, actual.getFkDepartamento());

		return cargosRepositorio.actualizarEstado(id, actualizado);
	}

	@Override
	public boolean nombreExiste(String nombre) {
		return cargosRepositorio.existeNombre(nombre.trim());
	}

	@Override
	public boolean nombreExisteParaOtro(String nombre, Integer idCargo) {
		return cargosRepositorio.existeNombreParaOtro(nombre.trim(), idCargo);
	}

}
