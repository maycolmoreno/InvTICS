package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IDepartamentosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.DuplicidadException;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.DepartamentoRepositorioPuerto;

public class DepartamentosUseCaseImpl implements IDepartamentosUseCase {

	private final DepartamentoRepositorioPuerto departamentoRepositorio;

	public DepartamentosUseCaseImpl(DepartamentoRepositorioPuerto departamentoRepositorio) {
		this.departamentoRepositorio = departamentoRepositorio;
	}

	@Override
	@CacheEvict(value = "departamentos", allEntries = true)
	public Departamentos crear(Departamentos departamento) {
		if (departamentoRepositorio.existeNombre(departamento.getNombre().trim())) {
			throw new DuplicidadException("Ya existe un departamento con ese nombre");
		}
		return departamentoRepositorio.guardar(departamento);
	}

	@Override
	public Departamentos obtenerPorId(int id) {
		return departamentoRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));
	}

	@Override
	@Cacheable("departamentos")
	public List<Departamentos> listar() {
		return departamentoRepositorio.listarTodos();
	}

	@Override
	@CacheEvict(value = "departamentos", allEntries = true)
	public Departamentos actualizar(int id, Departamentos departamento) {
		departamentoRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));

		if (departamentoRepositorio.existeNombreParaOtro(departamento.getNombre().trim(), id)) {
			throw new DuplicidadException("Ya existe otro departamento con ese nombre");
		}

		Departamentos actualizado = new Departamentos(id, departamento.getNombre(), departamento.isEstado());

		return departamentoRepositorio.actualizar(id, actualizado);
	}

	@Override
	@CacheEvict(value = "departamentos", allEntries = true)
	public Departamentos actualizarEstado(int id, boolean estado) {

		Departamentos actual = departamentoRepositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));

		// ✅ Solo cambia el estado, mantiene nombre/agencia
		Departamentos actualizado = new Departamentos(actual.getIdDepartamento(),
				actual.getNombre(), estado);

		return departamentoRepositorio.actualizarEstado(id, actualizado);
	}

	@Override
	public boolean nombreExiste(String nombre) {
		return departamentoRepositorio.existeNombre(nombre.trim());
	}

	@Override
	public boolean nombreExisteParaOtro(String nombre, Integer idDepartamento) {
		return departamentoRepositorio.existeNombreParaOtro(nombre.trim(), idDepartamento);
	}

}
