package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import com.uisrael.gestionactivosapi.dominio.repositorios.IUsuariosRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IUsuariosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;

public class UsuariosRepositorioImpl implements IUsuariosRepositorio {

	private final IUsuariosJpaRepositorio jpaRepository;

	private final IUsuariosJpaMapper entityMapper;

	public UsuariosRepositorioImpl(IUsuariosJpaRepositorio jpaRepository, IUsuariosJpaMapper entityMapper) {
		this.jpaRepository = jpaRepository;
		this.entityMapper = entityMapper;
	}

	@Override
	public Usuarios guardar(Usuarios usuario) {
		UsuariosJpa entity = entityMapper.toEntity(usuario);
		UsuariosJpa guardado = jpaRepository.save(entity);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Optional<Usuarios> buscarPorId(int id) {
		return jpaRepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<Usuarios> listarTodos() {
		return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		UsuariosJpa entity = jpaRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
		entity.setEstado(false);
		jpaRepository.save(entity);
	}

	@Override
	public Optional<Usuarios> buscarPorCorreo(String correo) {
		return jpaRepository.findByCorreo(correo).map(entityMapper::toDomain);
	}

}
