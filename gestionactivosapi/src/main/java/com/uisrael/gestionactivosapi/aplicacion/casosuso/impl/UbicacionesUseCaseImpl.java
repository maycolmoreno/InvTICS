package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IUbicacionesUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UbicacionRepositorioPuerto;

public class UbicacionesUseCaseImpl implements IUbicacionesUseCase {

	private final UbicacionRepositorioPuerto ubicacionRepositorio;

	public UbicacionesUseCaseImpl(UbicacionRepositorioPuerto ubicacionRepositorio) {
		this.ubicacionRepositorio = ubicacionRepositorio;
	}

	@Override
	public Ubicaciones crear(Ubicaciones ubicacion) {
		return ubicacionRepositorio.guardar(ubicacion);
	}

	@Override
	public Ubicaciones obtenerPorId(int id) {
		return ubicacionRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Ubicación no encontrada"));
	}

	@Override
	public List<Ubicaciones> listar() {
		return ubicacionRepositorio.listarTodos();
	}

	@Override
	public Ubicaciones actualizar(int id, Ubicaciones ubicacion) {
		ubicacionRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Ubicación no encontrada"));

		Ubicaciones actualizado = new Ubicaciones(id, ubicacion.getNombre(), ubicacion.getAgencia(),
				ubicacion.isEstado(), ubicacion.getLatitud(), ubicacion.getLongitud(),
				ubicacion.getDireccion(), ubicacion.getCiudad(), ubicacion.getParroquia(),
				ubicacion.getProvincia(), ubicacion.getLinkCoordenada());

		return ubicacionRepositorio.actualizar(id, actualizado);
	}

	@Override
	public Ubicaciones actualizarEstado(int id, boolean estado) {

		Ubicaciones actual = ubicacionRepositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Ubicación no encontrada"));

		// ✅ Solo cambia el estado, mantiene nombre/agencia y geo
		Ubicaciones actualizado = new Ubicaciones(actual.getIdUbicacion(),
				actual.getNombre(), actual.getAgencia(), estado,
				actual.getLatitud(), actual.getLongitud(), actual.getDireccion(),
				actual.getCiudad(), actual.getParroquia(), actual.getProvincia(),
				actual.getLinkCoordenada());

		return ubicacionRepositorio.actualizarEstado(id, actualizado);
	}

	@Override
	public boolean nombreExiste(String nombre) {
		return ubicacionRepositorio.existeNombre(nombre.trim());
	}

	@Override
	public boolean nombreExisteParaOtro(String nombre, Integer idUbicacion) {
		return ubicacionRepositorio.existeNombreParaOtro(nombre.trim(), idUbicacion);
	}

}
