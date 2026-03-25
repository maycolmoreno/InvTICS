package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMarcasUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.MarcaRepositorioPuerto;


public class MarcasUseCaseImpl implements IMarcasUseCase {

	private final MarcaRepositorioPuerto marcaRepositorio;

	public MarcasUseCaseImpl(MarcaRepositorioPuerto marcaRepositorio) {
		this.marcaRepositorio = marcaRepositorio;
	}

	@Override
	public Marcas crear(Marcas Marcas) {
		return marcaRepositorio.guardar(Marcas);
	}

	@Override
	public Marcas obtenerPorId(int id) {
		return marcaRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Marca no encontrada"));
	}

	@Override
	public List<Marcas> listar() {
		return marcaRepositorio.listarTodos();
	}

	@Override
	public Marcas actualizar(int id, Marcas marcas) {
		return marcaRepositorio.actualizar(id, marcas);
	}

	@Override
	public void eliminar(int id) {
		marcaRepositorio.eliminar(id);
	}


}
