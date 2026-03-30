package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.ActividadRealizada;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActividadRealizadaRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadRealizadaJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadRealizadaJpaRepositorio;

public class ActividadRealizadaRepositorioImpl implements ActividadRealizadaRepositorioPuerto {

	private final IActividadRealizadaJpaRepositorio jpaRepositorio;

	public ActividadRealizadaRepositorioImpl(IActividadRealizadaJpaRepositorio jpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
	}

	@Override
	public void eliminarPorMantenimiento(int idMantenimiento) {
		jpaRepositorio.deleteByIdMantenimiento(idMantenimiento);
	}

	@Override
	public List<ActividadRealizada> guardarTodas(List<ActividadRealizada> actividades) {
		return jpaRepositorio.saveAll(actividades.stream().map(this::toEntity).toList()).stream().map(this::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public List<ActividadRealizada> listarPorMantenimiento(int idMantenimiento) {
		return jpaRepositorio.findAllByIdMantenimiento(idMantenimiento).stream().map(this::toDomain)
				.collect(Collectors.toList());
	}

	private ActividadRealizada toDomain(ActividadRealizadaJpa entity) {
		ActividadRealizada actividad = new ActividadRealizada();
		actividad.setIdActividadRealizada(entity.getIdActividadRealizada());
		actividad.setIdMantenimiento(entity.getIdMantenimiento());
		actividad.setIdActividad(entity.getIdActividad());
		actividad.setRealizada(Boolean.TRUE.equals(entity.getRealizada()));
		return actividad;
	}

	private ActividadRealizadaJpa toEntity(ActividadRealizada actividad) {
		ActividadRealizadaJpa entity = new ActividadRealizadaJpa();
		entity.setIdActividadRealizada(actividad.getIdActividadRealizada());
		entity.setIdMantenimiento(actividad.getIdMantenimiento());
		entity.setIdActividad(actividad.getIdActividad());
		entity.setRealizada(actividad.isRealizada());
		return entity;
	}
}
