package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import com.uisrael.gestionactivosapi.dominio.repositorios.IDepartamentosRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.DepartamentosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionesJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IDepartamentosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IDepartamentosJpaRepositorio;

public class DepartamentosRepositorioImpl implements IDepartamentosRepositorio{

	private final IDepartamentosJpaRepositorio jpaRepository;

	private final IDepartamentosJpaMapper entityMapper;

	public DepartamentosRepositorioImpl(IDepartamentosJpaRepositorio jpaRepository,
			IDepartamentosJpaMapper entityMapper) {
		this.jpaRepository = jpaRepository;
		this.entityMapper = entityMapper;
	}

	@Override
	public Departamentos guardar(Departamentos departamento) {
		DepartamentosJpa entity = entityMapper.toEntity(departamento);
		DepartamentosJpa guardado = jpaRepository.save(entity);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Optional<Departamentos> buscarPorId(int id) {
		return jpaRepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<Departamentos> listarTodos() {
		return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	@Override
	public Departamentos actualizar(int id, Departamentos departamento) {
		DepartamentosJpa existente = jpaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));

		existente.setNombre(departamento.getNombre());
		existente.setEstado(departamento.isEstado());

		// Actualizar ubicacion por id si viene
		if (departamento.getFkUbicacion() != null) {
			UbicacionesJpa ubi = new UbicacionesJpa();
			ubi.setIdUbicacion(departamento.getFkUbicacion().getIdUbicacion());
			existente.setFkUbicacion(ubi);
		}

		DepartamentosJpa guardado = jpaRepository.save(existente);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Departamentos actualizarEstado(int id, Departamentos departamento) {
		DepartamentosJpa existente = jpaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));

		existente.setEstado(departamento.isEstado());

		DepartamentosJpa guardado = jpaRepository.save(existente);
		return entityMapper.toDomain(guardado);
	}

    @Override
    public boolean existeNombre(String nombre) {
        return jpaRepository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    public boolean existeNombreParaOtro(String nombre, int idDepartamento) {
        return jpaRepository.existsByNombreIgnoreCaseAndIdDepartamentoNot(nombre, idDepartamento);
    }

}
