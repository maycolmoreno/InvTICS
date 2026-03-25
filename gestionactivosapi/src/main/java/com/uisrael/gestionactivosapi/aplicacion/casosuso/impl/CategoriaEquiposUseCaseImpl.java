package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICategoriaEquiposUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.CategoriaEquipos;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CategoriaRepositorioPuerto;

public class CategoriaEquiposUseCaseImpl implements ICategoriaEquiposUseCase {

	private final CategoriaRepositorioPuerto categoriaRepositorio;

	public CategoriaEquiposUseCaseImpl(CategoriaRepositorioPuerto categoriaRepositorio) {
		this.categoriaRepositorio = categoriaRepositorio;
	}

	@Override
	public CategoriaEquipos crear(CategoriaEquipos categoriaEquipo) {
		String nombreNormalizado = categoriaEquipo.getNombre().trim().toLowerCase();

		List<CategoriaEquipos> todasLasCategorias = categoriaRepositorio.listarTodos();
		boolean existe = todasLasCategorias.stream()
			.anyMatch(c -> c.getNombre().trim().toLowerCase().equals(nombreNormalizado));

		if (existe) {
			throw new IllegalArgumentException("Ya existe una categoría con el nombre '" + categoriaEquipo.getNombre());
		}

		return categoriaRepositorio.guardar(categoriaEquipo);
	}

	@Override
	public CategoriaEquipos obtenerPorId(int id) {
		return categoriaRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Categoría de equipo no encontrada"));
	}

	@Override
	public List<CategoriaEquipos> listar() {
		return categoriaRepositorio.listarTodos();
	}

	@Override
	public CategoriaEquipos actualizar(CategoriaEquipos categoriaEquipo) {
		if (categoriaRepositorio.buscarPorId(categoriaEquipo.getIdCategoria()).isEmpty()) {
			throw new RecursoNoEncontradoException("Categoría no encontrada con ID: " + categoriaEquipo.getIdCategoria());
		}

		String nombreNormalizado = categoriaEquipo.getNombre().trim().toLowerCase();
		List<CategoriaEquipos> todasLasCategorias = categoriaRepositorio.listarTodos();

		boolean existeOtra = todasLasCategorias.stream()
			.anyMatch(c -> c.getIdCategoria() != categoriaEquipo.getIdCategoria() &&
			             c.getNombre().trim().toLowerCase().equals(nombreNormalizado));

		if (existeOtra) {
			throw new IllegalArgumentException("Ya existe otra categoría con el nombre '" + categoriaEquipo.getNombre());
		}

		return categoriaRepositorio.guardar(categoriaEquipo);
	}

	@Override
	public void eliminar(int id) {
		CategoriaEquipos categoria = categoriaRepositorio.buscarPorId(id)
			.orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id));

		if (!categoria.isEstado()) {
			throw new IllegalArgumentException("Esta categoría ya se encuentra inactiva");
		}
		CategoriaEquipos categoriaInactiva = new CategoriaEquipos(categoria.getIdCategoria(), categoria.getNombre(), false);
		categoriaRepositorio.guardar(categoriaInactiva);
	}

}
