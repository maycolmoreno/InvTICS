package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.CategoriaEquipos;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CategoriaRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CategoriaEquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICategoriaEquiposJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICategoriaEquiposJpaRepositorio;

public class CategoriaEquiposRepositorioImpl implements CategoriaRepositorioPuerto {

	private final ICategoriaEquiposJpaRepositorio jpaRepositorio;
	private final ICategoriaEquiposJpaMapper mapper;

	public CategoriaEquiposRepositorioImpl(ICategoriaEquiposJpaRepositorio jpaRepositorio, 
			ICategoriaEquiposJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public CategoriaEquipos guardar(CategoriaEquipos categoriaEquipo) {
		CategoriaEquiposJpa jpa = mapper.toEntity(categoriaEquipo);
		CategoriaEquiposJpa saved = jpaRepositorio.save(jpa);
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<CategoriaEquipos> buscarPorId(int id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<CategoriaEquipos> listarTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Optional<CategoriaEquipos> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<CategoriaEquipos> obtenerTodas() {
		return listarTodos();
	}

	public Optional<CategoriaEquipos> buscarPorNombre(String nombre) {
		return jpaRepositorio.findByNombre(nombre).map(mapper::toDomain);
	}

	@Override
	public CategoriaEquipos actualizar(CategoriaEquipos categoria) {
		return guardar(categoria);
	}

	@Override
	public void eliminar(Integer id) {
		jpaRepositorio.deleteById(id);
	}

	@Override
	public Optional<CategoriaEquipos> obtenerPorNombre(String nombre) {
		return buscarPorNombre(nombre);
	}

	@Override
	public List<CategoriaEquipos> obtenerActivas() {
		return listarTodos().stream().filter(CategoriaEquipos::isEstado).collect(Collectors.toList());
	}
}
