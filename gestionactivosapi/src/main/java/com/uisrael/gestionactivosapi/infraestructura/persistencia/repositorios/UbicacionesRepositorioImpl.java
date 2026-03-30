package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UbicacionRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionesJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IUbicacionesJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUbicacionesJpaRepositorio;

public class UbicacionesRepositorioImpl implements UbicacionRepositorioPuerto {

	private final IUbicacionesJpaRepositorio jpaRepositorio;
	private final IUbicacionesJpaMapper mapper;

	public UbicacionesRepositorioImpl(IUbicacionesJpaRepositorio jpaRepositorio, 
			IUbicacionesJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public Ubicaciones guardar(Ubicaciones ubicacion) {
		UbicacionesJpa jpa = mapper.toEntity(ubicacion);
		UbicacionesJpa saved = jpaRepositorio.save(jpa);
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<Ubicaciones> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<Ubicaciones> obtenerTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Ubicaciones actualizar(int id, Ubicaciones ubicacion) {
		return mapper.toDomain(jpaRepositorio.save(mapper.toEntity(ubicacion)));
	}

	@Override
	public Ubicaciones actualizarEstado(int id, Ubicaciones ubicacion) {
		return mapper.toDomain(jpaRepositorio.save(mapper.toEntity(ubicacion)));
	}

	@Override
	public boolean existeNombre(String nombre) {
		return jpaRepositorio.existsByNombreIgnoreCase(nombre);
	}

	@Override
	public boolean existeNombreParaOtro(String nombre, int idUbicacion) {
		return jpaRepositorio.existsByNombreIgnoreCaseAndIdUbicacionNot(nombre, idUbicacion);
	}
}
