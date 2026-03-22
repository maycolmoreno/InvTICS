package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.dominio.repositorios.ICustodiasRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiasJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionesJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICustodiasJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiasJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUbicacionesJpaRepositorio;

public class CustodiasRepositorioImpl implements ICustodiasRepositorio {

	private final ICustodiasJpaRepositorio jpaRepository;
	private final ICustodiasJpaMapper entityMapper;

	private final IEquiposJpaRepositorio equiposRepo;
	private final ICustodiosJpaRepositorio custodiosRepo;
	private final IUbicacionesJpaRepositorio ubicacionesRepo;

	// 👉 Constructor usado por @Bean en ConfiguracionGeneral
	public CustodiasRepositorioImpl(ICustodiasJpaRepositorio jpaRepository, ICustodiasJpaMapper entityMapper,
			IEquiposJpaRepositorio equiposRepo, ICustodiosJpaRepositorio custodiosRepo,
			IUbicacionesJpaRepositorio ubicacionesRepo) {
		this.jpaRepository = jpaRepository;
		this.entityMapper = entityMapper;
		this.equiposRepo = equiposRepo;
		this.custodiosRepo = custodiosRepo;
		this.ubicacionesRepo = ubicacionesRepo;
	}

	// =========================
	// CREAR
	// =========================
	@Override
	public Custodias guardar(Custodias custodia) {

		CustodiasJpa entity = entityMapper.toEntity(custodia);

		if (custodia.getFkEquipo() != null) {
			entity.setFkEquipo(equiposRepo.getReferenceById(custodia.getFkEquipo().getIdEquipo()));
		}

		if (custodia.getFkCustodio() != null) {
			CustodiosJpa custodio = custodiosRepo.getReferenceById(custodia.getFkCustodio().getIdCustodio());
			entity.setFkCustodio(custodio);
			// Fijar la ubicacion de la custodia desde el custodio cuando no llegue explicita.
			if (entity.getFkUbicacion() == null && custodio.getFkUbicacion() != null) {
				entity.setFkUbicacion(custodio.getFkUbicacion());
			}
		}

		if (custodia.getFkUbicacion() != null) {
			entity.setFkUbicacion(ubicacionesRepo.getReferenceById(custodia.getFkUbicacion().getIdUbicacion()));
		}

		CustodiasJpa guardado = jpaRepository.save(entity);

		// 🔑 volver a leer para traer relaciones completas
		CustodiasJpa completo = jpaRepository.findById(guardado.getIdCustodiaEquipo())
				.orElseThrow(() -> new RecursoNoEncontradoException("No se pudo leer la custodia guardada"));

		return entityMapper.toDomain(completo);
	}

	// =========================
	// BUSCAR POR ID
	// =========================
	@Override
	public Optional<Custodias> buscarPorId(int id) {
		return jpaRepository.findById(id).map(entityMapper::toDomain);
	}

	// =========================
	// LISTAR
	// =========================
	@Override
	public List<Custodias> listarTodos() {
		return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	// =========================
	// ACTUALIZAR
	// =========================
	@Override
	public Custodias actualizar(int id, Custodias custodia) {

		CustodiasJpa existente = jpaRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Custodia no encontrada"));

		existente.setFechaInicio(custodia.getFechaInicio());
		existente.setFechaFin(custodia.getFechaFin());
		existente.setObservacion(custodia.getObservacion());
		existente.setEstado(custodia.isEstado());

		if (custodia.getTipoMovimiento() != null) {
			existente.setTipoMovimiento(custodia.getTipoMovimiento());
		}

		if (custodia.getFkEquipo() != null) {
			existente.setFkEquipo(equiposRepo.getReferenceById(custodia.getFkEquipo().getIdEquipo()));
		}

		if (custodia.getFkCustodio() != null) {
			CustodiosJpa custodio = custodiosRepo.getReferenceById(custodia.getFkCustodio().getIdCustodio());
			existente.setFkCustodio(custodio);
			if (custodia.getFkUbicacion() == null) {
				existente.setFkUbicacion(custodio.getFkUbicacion());
			}
		}

		if (custodia.getFkUbicacion() != null) {
			UbicacionesJpa ubicacion = ubicacionesRepo.getReferenceById(custodia.getFkUbicacion().getIdUbicacion());
			existente.setFkUbicacion(ubicacion);
		}

		CustodiasJpa guardado = jpaRepository.save(existente);

		CustodiasJpa completo = jpaRepository.findById(guardado.getIdCustodiaEquipo())
				.orElseThrow(() -> new RecursoNoEncontradoException("No se pudo leer la custodia actualizada"));

		return entityMapper.toDomain(completo);
	}

	// =========================
	// ACTUALIZAR ESTADO
	// =========================
	@Override
	public Custodias actualizarEstado(int id, Custodias custodia) {

		CustodiasJpa existente = jpaRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Custodia no encontrada"));

		existente.setEstado(custodia.isEstado());

		CustodiasJpa guardado = jpaRepository.save(existente);

		CustodiasJpa completo = jpaRepository.findById(guardado.getIdCustodiaEquipo())
				.orElseThrow(() -> new RecursoNoEncontradoException("No se pudo leer la custodia"));

		return entityMapper.toDomain(completo);
	}

	@Override
	public boolean existeCustodiaActivaPorEquipo(int idEquipo) {
		return jpaRepository.existsByFkEquipo_IdEquipoAndEstadoTrue(idEquipo);
	}

	@Override
	public boolean existeCustodiaActivaPorEquipoParaOtroRegistro(int idEquipo, int idCustodiaEquipo) {
		return jpaRepository.existsByFkEquipo_IdEquipoAndEstadoTrueAndIdCustodiaEquipoNot(idEquipo, idCustodiaEquipo);
	}

	@Override
	public long contarPorTipoMovimiento(String tipoMovimiento) {
		return jpaRepository.countByTipoMovimiento(tipoMovimiento);
	}

	@Override
	public Optional<Custodias> buscarActivaPorEquipo(int idEquipo) {
		return jpaRepository
				.findFirstByFkEquipo_IdEquipoAndEstadoTrueAndFechaFinIsNullOrderByIdCustodiaEquipoDesc(idEquipo)
				.map(entityMapper::toDomain);
	}
}
