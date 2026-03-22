package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.CategoriaEquipos;
import com.uisrael.gestionactivosapi.dominio.repositorios.ICategoriaEquiposRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CategoriaEquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICategoriaEquiposJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICategoriaEquiposJpaRepositorio;

public class CategoriaEquiposRepositorioImpl implements ICategoriaEquiposRepositorio {

	private final ICategoriaEquiposJpaRepositorio jpaRepository;

	private final ICategoriaEquiposJpaMapper entityMapper;

	public CategoriaEquiposRepositorioImpl(ICategoriaEquiposJpaRepositorio jpaRepository, ICategoriaEquiposJpaMapper entityMapper) {
		this.jpaRepository = jpaRepository;
		this.entityMapper = entityMapper;
	}

	@Override
	public CategoriaEquipos guardar(CategoriaEquipos categoriaEquipo) {
		CategoriaEquiposJpa entity = entityMapper.toEntity(categoriaEquipo);
		CategoriaEquiposJpa guardado = jpaRepository.save(entity);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Optional<CategoriaEquipos> buscarPorId(int id) {
		return jpaRepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<CategoriaEquipos> listarTodos() {
		return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		CategoriaEquiposJpa entity = jpaRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));
		entity.setEstado(false);
		jpaRepository.save(entity);
	}

	@Override
	public Optional<CategoriaEquipos> buscarPorNombre(String nombre) {
		return jpaRepository.findByNombre(nombre).map(entityMapper::toDomain);
	}

}
