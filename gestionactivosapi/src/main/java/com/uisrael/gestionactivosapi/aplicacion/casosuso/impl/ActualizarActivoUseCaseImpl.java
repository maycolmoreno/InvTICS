package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IActualizarActivoUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Activo;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActivoRepositorioPuerto;

public class ActualizarActivoUseCaseImpl implements IActualizarActivoUseCase {

	private final ActivoRepositorioPuerto activoRepositorio;

	public ActualizarActivoUseCaseImpl(ActivoRepositorioPuerto activoRepositorio) {
		this.activoRepositorio = activoRepositorio;
	}

	@Override
	public void ejecutar(Activo activo) {
		activoRepositorio.actualizar(activo);
	}
}
