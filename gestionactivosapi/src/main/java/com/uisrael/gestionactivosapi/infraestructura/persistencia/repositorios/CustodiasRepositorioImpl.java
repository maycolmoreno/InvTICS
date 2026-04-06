package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.dominio.modelo.Pagina;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodiasRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiasJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICustodiasJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiasJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUbicacionesJpaRepositorio;

public class CustodiasRepositorioImpl implements CustodiasRepositorioPuerto {

	private final ICustodiasJpaRepositorio jpaRepositorio;
	private final ICustodiasJpaMapper mapper;
	private final IEquiposJpaRepositorio equiposRepo;
	private final ICustodiosJpaRepositorio custodiosRepo;
	private final IUbicacionesJpaRepositorio ubicacionesRepo;

	public CustodiasRepositorioImpl(ICustodiasJpaRepositorio jpaRepositorio, 
			ICustodiasJpaMapper mapper,
			IEquiposJpaRepositorio equiposRepo,
			ICustodiosJpaRepositorio custodiosRepo,
			IUbicacionesJpaRepositorio ubicacionesRepo) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
		this.equiposRepo = equiposRepo;
		this.custodiosRepo = custodiosRepo;
		this.ubicacionesRepo = ubicacionesRepo;
	}

	@Override
	public Custodias guardar(Custodias custodia) {
		CustodiasJpa jpa = mapper.toEntity(custodia);
		CustodiasJpa saved = jpaRepositorio.save(jpa);
		return mapper.toDomain(saved);
	}

	@Override
	public Custodias actualizar(int id, Custodias custodia) {
		return mapper.toDomain(jpaRepositorio.save(mapper.toEntity(custodia)));
	}

	@Override
	public Custodias actualizarEstado(int id, Custodias custodia) {
		return mapper.toDomain(jpaRepositorio.save(mapper.toEntity(custodia)));
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
	public Pagina<Custodias> listarPaginado(int pagina, int tamanio) {
		Page<CustodiasJpa> page = jpaRepositorio.findAll(
				PageRequest.of(pagina, tamanio, Sort.by("idCustodiaEquipo").descending()));
		List<Custodias> contenido = page.getContent().stream().map(mapper::toDomain).collect(Collectors.toList());
		return new Pagina<>(contenido, page.getNumber(), page.getSize(),
				page.getTotalElements(), page.getTotalPages());
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
	public long contarPorTipoMovimiento(String tipoMovimiento) {
		return jpaRepositorio.countByTipoMovimiento(tipoMovimiento);
	}

	@Override
	public Optional<Custodias> buscarActivaPorEquipo(int idEquipo) {
		return jpaRepositorio.findFirstByFkEquipo_IdEquipoAndEstadoTrueAndFechaFinIsNullOrderByIdCustodiaEquipoDesc(idEquipo)
				.map(mapper::toDomain);
	}
}
