package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Cargos;
import com.uisrael.gestionactivosapi.dominio.repositorios.ICargosRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CargosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.DepartamentosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICargosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICargosJpaRepositorio;

public class CargosRepositorioImpl implements ICargosRepositorio{

	private final ICargosJpaRepositorio jpaRepository;

	private final ICargosJpaMapper entityMapper;


	public CargosRepositorioImpl(ICargosJpaRepositorio jpaRepository, ICargosJpaMapper entityMapper) {
		this.jpaRepository = jpaRepository;
		this.entityMapper = entityMapper;
	}

	@Override
	public Cargos guardar(Cargos cargo) {
		CargosJpa entity = entityMapper.toEntity(cargo);
		CargosJpa guardado = jpaRepository.save(entity);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Optional<Cargos> buscarPorId(int id) {
		return jpaRepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<Cargos> listarTodos() {
		return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	@Override
	public Cargos actualizar(int id, Cargos cargo) {
		CargosJpa existente = jpaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Cargo no encontrado"));

		existente.setNombre(cargo.getNombre());
		existente.setEstado(cargo.isEstado());

		// Actualizar departamento por id si viene
		if (cargo.getFkDepartamento() != null) {
			DepartamentosJpa dep = new DepartamentosJpa();
			dep.setIdDepartamento(cargo.getFkDepartamento().getIdDepartamento());
			existente.setFkDepartamento(dep);
		}

		CargosJpa guardado = jpaRepository.save(existente);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Cargos actualizarEstado(int id, Cargos cargo) {
		CargosJpa existente = jpaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Cargo no encontrado"));

		existente.setEstado(cargo.isEstado());

		CargosJpa guardado = jpaRepository.save(existente);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public boolean existeNombre(String nombre) {
		return jpaRepository.existsByNombreIgnoreCase(nombre);
	}

	@Override
	public boolean existeNombreParaOtro(String nombre, int idCargo) {
		return jpaRepository.existsByNombreIgnoreCaseAndIdCargoNot(nombre, idCargo);
	}

}
