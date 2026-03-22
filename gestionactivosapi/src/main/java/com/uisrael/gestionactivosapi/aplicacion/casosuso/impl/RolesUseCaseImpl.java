package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRolesUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Roles;
import com.uisrael.gestionactivosapi.dominio.repositorios.IRolesRepositorio;

public class RolesUseCaseImpl implements IRolesUseCase {

	private final IRolesRepositorio repositorio;

	public RolesUseCaseImpl(IRolesRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Roles crear(Roles rol) {
		if (repositorio.buscarPorNombre(rol.getNombre()).isPresent()) {
			throw new IllegalArgumentException("Ya existe un rol con el nombre '" + rol.getNombre() + "'");
		}
		return repositorio.guardar(rol);
	}

	@Override
	public Roles obtenerPorId(int id) {
		return repositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado"));
	}

	@Override
	public List<Roles> listar() {
		return repositorio.listarTodos();
	}

	@Override
	public Roles actualizar(Roles rol) {
		if (repositorio.buscarPorId(rol.getIdRol()).isEmpty()) {
			throw new RecursoNoEncontradoException("Rol no encontrado con ID: " + rol.getIdRol());
		}

		Optional<Roles> rolExistente = repositorio.buscarPorNombre(rol.getNombre());
		if (rolExistente.isPresent() && rolExistente.get().getIdRol() != rol.getIdRol()) {
			throw new IllegalArgumentException("Ya existe otro rol con el nombre '" + rol.getNombre() + "'");
		}

		return repositorio.guardar(rol);
	}

	@Override
	public void eliminar(int id) {
		Roles rol = repositorio.buscarPorId(id)
			.orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con ID: " + id));

		if (!rol.isEstado()) {
			throw new IllegalArgumentException("Este rol ya se encuentra inactivo");
		}

		Roles rolInactivo = new Roles(rol.getIdRol(), rol.getNombre(), false);
		repositorio.guardar(rolInactivo);
	}

}
