package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICategoriaEquiposUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.CategoriaEquipos;
import com.uisrael.gestionactivosapi.dominio.repositorios.ICategoriaEquiposRepositorio;

public class CategoriaEquiposUseCaseImpl implements ICategoriaEquiposUseCase {

	private final ICategoriaEquiposRepositorio repositorio;

	public CategoriaEquiposUseCaseImpl(ICategoriaEquiposRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public CategoriaEquipos crear(CategoriaEquipos categoriaEquipo) {
		String nombreNormalizado = categoriaEquipo.getNombre().trim().toLowerCase();

		List<CategoriaEquipos> todasLasCategorias = repositorio.listarTodos();
		boolean existe = todasLasCategorias.stream()
			.anyMatch(c -> c.getNombre().trim().toLowerCase().equals(nombreNormalizado));

		if (existe) {
			throw new IllegalArgumentException("Ya existe una categoría con el nombre '" + categoriaEquipo.getNombre());
		}

		return repositorio.guardar(categoriaEquipo);
	}

	@Override
	public CategoriaEquipos obtenerPorId(int id) {
		return repositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Categoría de equipo no encontrada"));
	}

	@Override
	public List<CategoriaEquipos> listar() {
		return repositorio.listarTodos();
	}

	@Override
	public CategoriaEquipos actualizar(CategoriaEquipos categoriaEquipo) {
		if (repositorio.buscarPorId(categoriaEquipo.getIdCategoria()).isEmpty()) {
			throw new RecursoNoEncontradoException("Categoría no encontrada con ID: " + categoriaEquipo.getIdCategoria());
		}

		String nombreNormalizado = categoriaEquipo.getNombre().trim().toLowerCase();
		List<CategoriaEquipos> todasLasCategorias = repositorio.listarTodos();

		boolean existeOtra = todasLasCategorias.stream()
			.anyMatch(c -> c.getIdCategoria() != categoriaEquipo.getIdCategoria() &&
			             c.getNombre().trim().toLowerCase().equals(nombreNormalizado));

		if (existeOtra) {
			throw new IllegalArgumentException("Ya existe otra categoría con el nombre '" + categoriaEquipo.getNombre());
		}

		return repositorio.guardar(categoriaEquipo);
	}

	@Override
	public void eliminar(int id) {
		CategoriaEquipos categoria = repositorio.buscarPorId(id)
			.orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id));

		if (!categoria.isEstado()) {
			throw new IllegalArgumentException("Esta categoría ya se encuentra inactiva");
		}
		CategoriaEquipos categoriaInactiva = new CategoriaEquipos(categoria.getIdCategoria(), categoria.getNombre(), false);
		repositorio.guardar(categoriaInactiva);
	}

}
