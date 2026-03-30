package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.Cargos;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CargosRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CargosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICargosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICargosJpaRepositorio;

public class CargosRepositorioImpl implements CargosRepositorioPuerto {

	private final ICargosJpaRepositorio jpaRepositorio;
	private final ICargosJpaMapper mapper;

	public CargosRepositorioImpl(ICargosJpaRepositorio jpaRepositorio, 
			ICargosJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public Cargos guardar(Cargos cargo) {
		CargosJpa jpa = mapper.toEntity(cargo);
		CargosJpa saved = jpaRepositorio.save(jpa);
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<Cargos> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<Cargos> obtenerTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Cargos actualizar(int id, Cargos cargo) {
		return mapper.toDomain(jpaRepositorio.save(mapper.toEntity(cargo)));
	}

	@Override
	public Cargos actualizarEstado(int id, Cargos cargo) {
		return mapper.toDomain(jpaRepositorio.save(mapper.toEntity(cargo)));
	}

	@Override
	public boolean existeNombre(String nombre) {
		return jpaRepositorio.existsByNombreIgnoreCase(nombre);
	}

	@Override
	public boolean existeNombreParaOtro(String nombre, int idCargo) {
		return jpaRepositorio.existsByNombreIgnoreCaseAndIdCargoNot(nombre, idCargo);
	}
}
