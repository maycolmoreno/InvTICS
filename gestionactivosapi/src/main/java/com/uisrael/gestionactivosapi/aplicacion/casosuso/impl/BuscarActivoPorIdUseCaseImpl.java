package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.Optional;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IBuscarActivoPorIdUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Activo;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActivoRepositorioPuerto;

public class BuscarActivoPorIdUseCaseImpl implements IBuscarActivoPorIdUseCase {

	private final ActivoRepositorioPuerto activoRepositorio;

	public BuscarActivoPorIdUseCaseImpl(ActivoRepositorioPuerto activoRepositorio) {
		this.activoRepositorio = activoRepositorio;
	}

	@Override
	public Optional<Activo> ejecutar(int idActivo) {
		return activoRepositorio.buscarPorId(idActivo);
	}
}
