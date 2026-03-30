package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.MarcaRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MarcasJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IMarcasJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMarcasJpaRepositorio;

public class MarcasRepositorioImpl implements MarcaRepositorioPuerto {

	private final IMarcasJpaRepositorio jpaRepositorio;
	private final IMarcasJpaMapper mapper;

	public MarcasRepositorioImpl(IMarcasJpaRepositorio jpaRepositorio, 
			IMarcasJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public Marcas guardar(Marcas marca) {
		MarcasJpa jpa = mapper.toEntity(marca);
		MarcasJpa saved = jpaRepositorio.save(jpa);
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<Marcas> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<Marcas> obtenerTodas() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Marcas actualizar(int id, Marcas marca) {
		return mapper.toDomain(jpaRepositorio.save(mapper.toEntity(marca)));
	}

	@Override
	public void eliminar(Integer id) {
		jpaRepositorio.deleteById(id);
	}

	@Override
	public Optional<Marcas> obtenerPorNombre(String nombre) {
		return obtenerTodas().stream()
				.filter(marca -> marca.getNombre() != null && marca.getNombre().equalsIgnoreCase(nombre))
				.findFirst();
	}

	@Override
	public List<Marcas> obtenerActivas() {
		return obtenerTodas().stream().filter(Marcas::isEstado).collect(Collectors.toList());
	}
}
