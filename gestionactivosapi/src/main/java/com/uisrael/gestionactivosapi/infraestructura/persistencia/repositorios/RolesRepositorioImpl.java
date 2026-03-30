package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.Roles;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.RolRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RolesJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IRolesJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRolesJpaRepositorio;

public class RolesRepositorioImpl implements RolRepositorioPuerto {

	private final IRolesJpaRepositorio jpaRepositorio;
	private final IRolesJpaMapper mapper;

	public RolesRepositorioImpl(IRolesJpaRepositorio jpaRepositorio, 
			IRolesJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public Roles guardar(Roles rol) {
		RolesJpa jpa = mapper.toEntity(rol);
		RolesJpa saved = jpaRepositorio.save(jpa);
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<Roles> buscarPorId(int id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<Roles> listarTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Optional<Roles> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<Roles> obtenerTodos() {
		return listarTodos();
	}

	@Override
	public Optional<Roles> buscarPorNombre(String nombre) {
		return jpaRepositorio.findByNombre(nombre).map(mapper::toDomain);
	}

	@Override
	public Roles actualizar(int id, Roles rol) {
		return guardar(rol);
	}

	@Override
	public void eliminar(Integer id) {
		jpaRepositorio.deleteById(id);
	}

	@Override
	public Optional<Roles> obtenerPorNombre(String nombre) {
		return buscarPorNombre(nombre);
	}

	@Override
	public List<Roles> obtenerActivos() {
		return listarTodos().stream().filter(Roles::isEstado).collect(Collectors.toList());
	}
}
