package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Roles;
import com.uisrael.gestionactivosapi.dominio.repositorios.IRolesRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RolesJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IRolesJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRolesJpaRepositorio;

public class RolesRepositorioImpl implements IRolesRepositorio {

	private final IRolesJpaRepositorio jpaRepository;

	private final IRolesJpaMapper entityMapper;

	public RolesRepositorioImpl(IRolesJpaRepositorio jpaRepository, IRolesJpaMapper entityMapper) {
		this.jpaRepository = jpaRepository;
		this.entityMapper = entityMapper;
	}

	@Override
	public Roles guardar(Roles rol) {
		RolesJpa entity = entityMapper.toEntity(rol);
		RolesJpa guardado = jpaRepository.save(entity);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Optional<Roles> buscarPorId(int id) {
		return jpaRepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<Roles> listarTodos() {
		return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		RolesJpa entity = jpaRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado"));
		entity.setEstado(false);
		jpaRepository.save(entity);
	}

	@Override
	public Optional<Roles> buscarPorNombre(String nombre) {
		return jpaRepository.findByNombre(nombre).map(entityMapper::toDomain);
	}

}
