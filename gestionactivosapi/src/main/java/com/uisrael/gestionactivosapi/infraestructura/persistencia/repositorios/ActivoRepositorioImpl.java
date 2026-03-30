package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.Activo;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActivoRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActivoJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IActivoJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActivoJpaRepositorio;

public class ActivoRepositorioImpl implements ActivoRepositorioPuerto {

	private final IActivoJpaRepositorio jpaRepositorio;
	private final IActivoJpaMapper mapper;

	public ActivoRepositorioImpl(IActivoJpaRepositorio jpaRepositorio, IActivoJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public Activo guardar(Activo activo) {
		ActivoJpa jpa = mapper.toPersistence(activo);
		ActivoJpa saved = jpaRepositorio.save(jpa);
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<Activo> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id.longValue()).map(mapper::toDomain);
	}

	@Override
	public List<Activo> obtenerTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public void actualizar(Activo activo) {
		jpaRepositorio.save(mapper.toPersistence(activo));
	}

	@Override
	public void eliminar(Integer id) {
		jpaRepositorio.deleteById(id.longValue());
	}
}
