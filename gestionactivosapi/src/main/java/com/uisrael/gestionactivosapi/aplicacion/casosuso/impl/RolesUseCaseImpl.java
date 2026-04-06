package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRolesUseCase;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Roles;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.RolRepositorioPuerto;

public class RolesUseCaseImpl implements IRolesUseCase {

	private final RolRepositorioPuerto rolRepositorio;

	public RolesUseCaseImpl(RolRepositorioPuerto rolRepositorio) {
		this.rolRepositorio = rolRepositorio;
	}

	@Override
	@CacheEvict(value = "roles", allEntries = true)
	public Roles crear(Roles rol) {
		if (rolRepositorio.buscarPorNombre(rol.getNombre()).isPresent()) {
			throw new IllegalArgumentException("Ya existe un rol con el nombre '" + rol.getNombre() + "'");
		}
		return rolRepositorio.guardar(rol);
	}

	@Override
	public Roles obtenerPorId(int id) {
		return rolRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado"));
	}

	@Override
	@Cacheable("roles")
	public List<Roles> listar() {
		return rolRepositorio.listarTodos();
	}

	@Override
	@CacheEvict(value = "roles", allEntries = true)
	public Roles actualizar(Roles rol) {
		if (rolRepositorio.buscarPorId(rol.getIdRol()).isEmpty()) {
			throw new RecursoNoEncontradoException("Rol no encontrado con ID: " + rol.getIdRol());
		}

		Optional<Roles> rolExistente = rolRepositorio.buscarPorNombre(rol.getNombre());
		if (rolExistente.isPresent() && rolExistente.get().getIdRol() != rol.getIdRol()) {
			throw new IllegalArgumentException("Ya existe otro rol con el nombre '" + rol.getNombre() + "'");
		}

		return rolRepositorio.guardar(rol);
	}

	@Override
	@CacheEvict(value = "roles", allEntries = true)
	public void eliminar(int id) {
		Roles rol = rolRepositorio.buscarPorId(id)
			.orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con ID: " + id));

		if (!rol.isEstado()) {
			throw new IllegalArgumentException("Este rol ya se encuentra inactivo");
		}

		Roles rolInactivo = new Roles(rol.getIdRol(), rol.getNombre(), false);
		rolRepositorio.guardar(rolInactivo);
	}

}
