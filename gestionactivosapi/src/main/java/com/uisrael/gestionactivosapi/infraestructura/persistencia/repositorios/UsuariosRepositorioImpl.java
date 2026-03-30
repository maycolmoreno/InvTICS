package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UsuarioRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IUsuariosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;

public class UsuariosRepositorioImpl implements UsuarioRepositorioPuerto {

	private final IUsuariosJpaRepositorio jpaRepositorio;
	private final IUsuariosJpaMapper mapper;

	public UsuariosRepositorioImpl(IUsuariosJpaRepositorio jpaRepositorio, IUsuariosJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public Usuarios guardar(Usuarios usuario) {
		UsuariosJpa entidad = mapper.toEntity(usuario);
		UsuariosJpa guardado = jpaRepositorio.save(entidad);
		return mapper.toDomain(guardado);
	}

	@Override
	public Optional<Usuarios> buscarPorId(int id) {
		return obtenerPorId(id);
	}

	@Override
	public List<Usuarios> listarTodos() {
		return obtenerTodos();
	}

	@Override
	public Optional<Usuarios> buscarPorCorreo(String correo) {
		return obtenerPorCorreo(correo);
	}

	@Override
	public Optional<Usuarios> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<Usuarios> obtenerTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Usuarios actualizar(int id, Usuarios usuario) {
		return guardar(usuario);
	}

	@Override
	public void eliminar(Integer id) {
		jpaRepositorio.deleteById(id);
	}

	@Override
	public Optional<Usuarios> obtenerPorNombreUsuario(String nombreUsuario) {
		// El modelo actual no tiene username separado; usamos correo como identificador de acceso.
		return obtenerPorCorreo(nombreUsuario);
	}

	@Override
	public Optional<Usuarios> obtenerPorCorreo(String correo) {
		return jpaRepositorio.findByCorreo(correo).map(mapper::toDomain);
	}

	@Override
	public List<Usuarios> obtenerActivos() {
		return jpaRepositorio.findAllByEstadoTrue().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public List<Usuarios> obtenerPorRol(Integer rolId) {
		return jpaRepositorio.findAllByFkRol_IdRol(rolId).stream().map(mapper::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public boolean existePorCorreo(String correo) {
		return jpaRepositorio.existsByCorreoIgnoreCase(correo);
	}
}
