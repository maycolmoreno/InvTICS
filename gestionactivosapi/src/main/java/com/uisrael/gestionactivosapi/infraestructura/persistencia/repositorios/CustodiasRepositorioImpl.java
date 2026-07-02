package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodiasRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiasJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICustodiasJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiasJpaRepositorio;

import jakarta.persistence.EntityManager;

public class CustodiasRepositorioImpl implements CustodiasRepositorioPuerto {

	private final ICustodiasJpaRepositorio jpaRepositorio;
	private final ICustodiasJpaMapper mapper;
	private final EntityManager entityManager;

	public CustodiasRepositorioImpl(ICustodiasJpaRepositorio jpaRepositorio, 
			ICustodiasJpaMapper mapper,
			EntityManager entityManager) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
		this.entityManager = entityManager;
	}

	@Override
	public Custodias guardar(Custodias custodia) {
		CustodiasJpa jpa = mapper.toEntity(custodia);
		CustodiasJpa saved = jpaRepositorio.save(jpa);
		// Limpiar caché L1 para que findById recargue las relaciones completas
		jpaRepositorio.flush();
		entityManager.clear();
		CustodiasJpa completo = jpaRepositorio.findById(saved.getIdCustodiaEquipo()).orElse(saved);
		return mapper.toDomain(completo);
	}

	@Override
	public Custodias actualizar(int id, Custodias custodia) {
		CustodiasJpa saved = jpaRepositorio.save(mapper.toEntity(custodia));
		jpaRepositorio.flush();
		entityManager.clear();
		CustodiasJpa completo = jpaRepositorio.findById(saved.getIdCustodiaEquipo()).orElse(saved);
		return mapper.toDomain(completo);
	}

	@Override
	public Custodias actualizarEstado(int id, Custodias custodia) {
		CustodiasJpa saved = jpaRepositorio.save(mapper.toEntity(custodia));
		jpaRepositorio.flush();
		entityManager.clear();
		CustodiasJpa completo = jpaRepositorio.findById(saved.getIdCustodiaEquipo()).orElse(saved);
		return mapper.toDomain(completo);
	}

	@Override
	public Optional<Custodias> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<Custodias> obtenerTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public boolean existeCustodiaActivaPorEquipo(int idEquipo) {
		return jpaRepositorio.existsByFkEquipo_IdEquipoAndEstadoTrue(idEquipo);
	}

	@Override
	public boolean existeCustodiaActivaPorEquipoParaOtroRegistro(int idEquipo, int idCustodiaEquipo) {
		return jpaRepositorio.existsByFkEquipo_IdEquipoAndEstadoTrueAndIdCustodiaEquipoNot(idEquipo, idCustodiaEquipo);
	}

	@Override
	public Optional<Custodias> buscarActivaPorEquipo(int idEquipo) {
		return jpaRepositorio.findFirstByFkEquipo_IdEquipoAndEstadoTrueAndFechaFinIsNullOrderByIdCustodiaEquipoDesc(idEquipo)
				.map(mapper::toDomain);
	}

	@Override
	public List<Custodias> buscarPorGrupoActa(int idCustodio, String tipoMovimiento, java.time.LocalDate fechaInicio) {
		return jpaRepositorio.findByFkCustodio_IdCustodioAndTipoMovimientoAndFechaInicio(
				idCustodio, tipoMovimiento, fechaInicio)
				.stream().map(mapper::toDomain).collect(Collectors.toList());
	}
}
