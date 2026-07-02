package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodioRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICustodiosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;

public class CustodiosRepositorioImpl implements CustodioRepositorioPuerto {

	private final ICustodiosJpaRepositorio jpaRepositorio;
	private final ICustodiosJpaMapper mapper;

	public CustodiosRepositorioImpl(ICustodiosJpaRepositorio jpaRepositorio, 
			ICustodiosJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public Custodios guardar(Custodios custodio) {
		CustodiosJpa jpa = mapper.toEntity(custodio);
		CustodiosJpa saved = jpaRepositorio.save(jpa);
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<Custodios> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<Custodios> obtenerTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Custodios actualizar(int id, Custodios custodio) {
		return mapper.toDomain(jpaRepositorio.save(mapper.toEntity(custodio)));
	}

	@Override
	public Custodios actualizarEstado(int id, Custodios custodio) {
		return mapper.toDomain(jpaRepositorio.save(mapper.toEntity(custodio)));
	}

	@Override
	public boolean existeCorreo(String correo) {
		return jpaRepositorio.existsByCorreoIgnoreCase(correo);
	}

	@Override
	public boolean existeCorreoParaOtro(String correo, int idCustodio) {
		return jpaRepositorio.existsByCorreoIgnoreCaseAndIdCustodioNot(correo, idCustodio);
	}

	@Override
	public boolean existeCedula(String cedula) {
		return jpaRepositorio.existsByCedulaIgnoreCase(cedula);
	}

	@Override
	public boolean existeCedulaParaOtro(String cedula, int idCustodio) {
		return jpaRepositorio.existsByCedulaIgnoreCaseAndIdCustodioNot(cedula, idCustodio);
	}

	@Override
	public boolean existeUsuarioVinculado(int idUsuario) {
		return jpaRepositorio.existsByFkUsuario_IdUsuario(idUsuario);
	}

	@Override
	public void eliminar(Integer id) {
		jpaRepositorio.deleteById(id);
	}

	@Override
	public List<Custodios> obtenerPorEstado(boolean estado) {
		return obtenerTodos().stream().filter(custodio -> custodio.isEstado() == estado).collect(Collectors.toList());
	}

	@Override
	public Optional<Custodios> obtenerPorCedula(String cedula) {
		return obtenerTodos().stream()
				.filter(custodio -> custodio.getCedula() != null && custodio.getCedula().equalsIgnoreCase(cedula))
				.findFirst();
	}

	@Override
	public List<Custodios> obtenerPorDepartamento(Integer departamentoId) {
		return obtenerTodos().stream()
				.filter(custodio -> custodio.getFkDepartamento() != null
						&& custodio.getFkDepartamento().getIdDepartamento() == departamentoId)
				.collect(Collectors.toList());
	}

	@Override
	public List<Custodios> obtenerAutorizadosPorCategoria(Integer categoriaId) {
		return obtenerPorEstado(true);
	}
}
