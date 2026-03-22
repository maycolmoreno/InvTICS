package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMarcasUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;
import com.uisrael.gestionactivosapi.dominio.repositorios.IMarcasRepositorio;


public class MarcasUseCaseImpl implements IMarcasUseCase {

	private final IMarcasRepositorio repositorio;

	public MarcasUseCaseImpl(IMarcasRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Marcas crear(Marcas Marcas) {
		return repositorio.guardar(Marcas);
	}

	@Override
	public Marcas obtenerPorId(int id) {
		return repositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Marca no encontrada"));
	}

	@Override
	public List<Marcas> listar() {
		return repositorio.listarTodos();
	}

	@Override
	public Marcas actualizar(int id, Marcas marcas) {
		return repositorio.actualizar(id, marcas);
	}

	@Override
	public void eliminar(int id) {
		repositorio.eliminar(id);
	}


}
