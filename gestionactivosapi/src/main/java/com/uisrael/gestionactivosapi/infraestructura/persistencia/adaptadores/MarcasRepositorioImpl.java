package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;
import com.uisrael.gestionactivosapi.dominio.repositorios.IMarcasRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MarcasJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IMarcasJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMarcasJpaRepositorio;


public class MarcasRepositorioImpl implements IMarcasRepositorio {

private final IMarcasJpaRepositorio jpaRepository;

	private final IMarcasJpaMapper entityMapper;

	public MarcasRepositorioImpl(IMarcasJpaRepositorio jpaRepository,
			IMarcasJpaMapper entityMapper) {
		this.jpaRepository = jpaRepository;
		this.entityMapper = entityMapper;
	}

	@Override
	public Marcas guardar(Marcas marcas) {
		MarcasJpa entity = entityMapper.toEntity(marcas);
		MarcasJpa guardado = jpaRepository.save(entity);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Optional<Marcas> buscarPorId(int id) {
		return jpaRepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<Marcas> listarTodos() {
		return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	@Override
	public Marcas actualizar(int id, Marcas marcas) {
		MarcasJpa existente = jpaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Marca no encontrada"));

		existente.setNombre(marcas.getNombre());
		existente.setEstado(marcas.isEstado());


		MarcasJpa guardado = jpaRepository.save(existente);
		return entityMapper.toDomain(guardado);
	}

		@Override
		public void eliminar(int id) {
			MarcasJpa entity = jpaRepository.findById(id)
					.orElseThrow(() -> new RecursoNoEncontradoException("Marca no encontrada"));
			entity.setEstado(false);
			jpaRepository.save(entity);
		}

}
