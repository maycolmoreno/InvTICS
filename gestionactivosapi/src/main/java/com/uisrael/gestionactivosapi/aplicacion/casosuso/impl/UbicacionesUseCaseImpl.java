package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IUbicacionesUseCase;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UbicacionRepositorioPuerto;

public class UbicacionesUseCaseImpl implements IUbicacionesUseCase {

	private final UbicacionRepositorioPuerto ubicacionRepositorio;

	public UbicacionesUseCaseImpl(UbicacionRepositorioPuerto ubicacionRepositorio) {
		this.ubicacionRepositorio = ubicacionRepositorio;
	}

	@Override
	@CacheEvict(value = "ubicaciones", allEntries = true)
	public Ubicaciones crear(Ubicaciones ubicacion) {
		return ubicacionRepositorio.guardar(ubicacion);
	}

	@Override
	public Ubicaciones obtenerPorId(int id) {
		return ubicacionRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Ubicación no encontrada"));
	}

	@Override
	@Cacheable("ubicaciones")
	public List<Ubicaciones> listar() {
		return ubicacionRepositorio.listarTodos();
	}

	@Override
	@CacheEvict(value = "ubicaciones", allEntries = true)
	public Ubicaciones actualizar(int id, Ubicaciones ubicacion) {
		ubicacionRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Ubicación no encontrada"));

		Ubicaciones actualizado = new Ubicaciones(id, ubicacion.getNombre(), ubicacion.getAgencia(),
				ubicacion.isEstado(), ubicacion.getLatitud(), ubicacion.getLongitud(),
				ubicacion.getDireccion(), ubicacion.getCiudad(), ubicacion.getParroquia(),
				ubicacion.getProvincia(), ubicacion.getLinkCoordenada());
		actualizado.setFkDepartamento(ubicacion.getFkDepartamento());
		actualizado.setIdCustodioEncargado(ubicacion.getIdCustodioEncargado());

		return ubicacionRepositorio.actualizar(id, actualizado);
	}

	@Override
	@CacheEvict(value = "ubicaciones", allEntries = true)
	public Ubicaciones actualizarEstado(int id, boolean estado) {

		Ubicaciones actual = ubicacionRepositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Ubicación no encontrada"));

		// ✅ Solo cambia el estado, mantiene nombre/agencia y geo
		Ubicaciones actualizado = new Ubicaciones(actual.getIdUbicacion(),
				actual.getNombre(), actual.getAgencia(), estado,
				actual.getLatitud(), actual.getLongitud(), actual.getDireccion(),
				actual.getCiudad(), actual.getParroquia(), actual.getProvincia(),
				actual.getLinkCoordenada());
		actualizado.setFkDepartamento(actual.getFkDepartamento());
		actualizado.setIdCustodioEncargado(actual.getIdCustodioEncargado());

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
