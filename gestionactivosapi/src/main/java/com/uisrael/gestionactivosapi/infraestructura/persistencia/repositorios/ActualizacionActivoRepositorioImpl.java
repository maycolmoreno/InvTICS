package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.ActualizacionActivo;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActualizacionActivoRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActualizacionActivoJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IActualizacionActivoJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActualizacionActivoJpaRepositorio;

public class ActualizacionActivoRepositorioImpl implements ActualizacionActivoRepositorioPuerto {

	private final IActualizacionActivoJpaRepositorio jpaRepositorio;
	private final IActualizacionActivoJpaMapper mapper;

	public ActualizacionActivoRepositorioImpl(IActualizacionActivoJpaRepositorio jpaRepositorio,
			IActualizacionActivoJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public ActualizacionActivo guardar(ActualizacionActivo actualizacion) {
		ActualizacionActivoJpa jpa = mapper.toPersistence(actualizacion);
		ActualizacionActivoJpa saved = jpaRepositorio.save(jpa);
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<ActualizacionActivo> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id.longValue()).map(mapper::toDomain);
	}

	@Override
	public List<ActualizacionActivo> obtenerTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public void actualizar(ActualizacionActivo actualizacion) {
		jpaRepositorio.save(mapper.toPersistence(actualizacion));
	}

	@Override
	public void eliminar(Integer id) {
		jpaRepositorio.deleteById(id.longValue());
	}
}
