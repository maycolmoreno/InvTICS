package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.ActividadChecklist;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActividadChecklistRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadChecklistJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadChecklistJpaRepositorio;

public class ActividadChecklistRepositorioImpl implements ActividadChecklistRepositorioPuerto {

	private final IActividadChecklistJpaRepositorio jpaRepositorio;

	public ActividadChecklistRepositorioImpl(IActividadChecklistJpaRepositorio jpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
	}

	@Override
	public ActividadChecklist guardar(ActividadChecklist actividad) {
		ActividadChecklistJpa entity = toEntity(actividad);
		return toDomain(jpaRepositorio.save(entity));
	}

	@Override
	public Optional<ActividadChecklist> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id).map(this::toDomain);
	}

	@Override
	public List<ActividadChecklist> listarActivas() {
		return jpaRepositorio.findAllByEstadoTrueOrderByOrdenAsc().stream().map(this::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public List<ActividadChecklist> listarActivasPorCategoria(Integer idCategoria) {
		return jpaRepositorio.findActivasPorCategoria(idCategoria).stream().map(this::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public void eliminar(Integer id) {
		jpaRepositorio.deleteById(id);
	}

	private ActividadChecklist toDomain(ActividadChecklistJpa entity) {
		ActividadChecklist actividad = new ActividadChecklist();
		actividad.setIdActividad(entity.getIdActividad());
		actividad.setNombre(entity.getNombre());
		actividad.setOrden(entity.getOrden());
		actividad.setEstado(Boolean.TRUE.equals(entity.getEstado()));
		return actividad;
	}

	private ActividadChecklistJpa toEntity(ActividadChecklist actividad) {
		ActividadChecklistJpa entity = new ActividadChecklistJpa();
		entity.setIdActividad(actividad.getIdActividad());
		entity.setNombre(actividad.getNombre());
		entity.setOrden(actividad.getOrden());
		entity.setEstado(actividad.isEstado());
		return entity;
	}
}
