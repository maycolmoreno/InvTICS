package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.DepartamentoRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.DepartamentosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IDepartamentosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IDepartamentosJpaRepositorio;

public class DepartamentosRepositorioImpl implements DepartamentoRepositorioPuerto {

	private final IDepartamentosJpaRepositorio jpaRepositorio;
	private final IDepartamentosJpaMapper mapper;

	public DepartamentosRepositorioImpl(IDepartamentosJpaRepositorio jpaRepositorio, 
			IDepartamentosJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public Departamentos guardar(Departamentos departamento) {
		DepartamentosJpa jpa = mapper.toEntity(departamento);
		DepartamentosJpa saved = jpaRepositorio.save(jpa);
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<Departamentos> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<Departamentos> obtenerTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Departamentos actualizar(int id, Departamentos departamento) {
		DepartamentosJpa jpa = mapper.toEntity(departamento);
		DepartamentosJpa updated = jpaRepositorio.save(jpa);
		return mapper.toDomain(updated);
	}

	@Override
	public Departamentos actualizarEstado(int id, Departamentos departamento) {
		DepartamentosJpa jpa = mapper.toEntity(departamento);
		DepartamentosJpa updated = jpaRepositorio.save(jpa);
		return mapper.toDomain(updated);
	}

	@Override
	public boolean existeNombre(String nombre) {
		return jpaRepositorio.existsByNombreIgnoreCase(nombre);
	}

	@Override
	public boolean existeNombreParaOtro(String nombre, int idDepartamento) {
		return jpaRepositorio.existsByNombreIgnoreCaseAndIdDepartamentoNot(nombre, idDepartamento);
	}

	@Override
	public void eliminar(Integer id) {
		jpaRepositorio.deleteById(id);
	}

	@Override
	public Optional<Departamentos> obtenerPorNombre(String nombre) {
		return jpaRepositorio.findByNombreIgnoreCase(nombre).map(mapper::toDomain);
	}

	@Override
	public List<Departamentos> obtenerActivos() {
		return jpaRepositorio.findAllByEstadoTrue().stream().map(mapper::toDomain).collect(Collectors.toList());
	}
}
