package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.MantenimientoRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IMantenimientosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientosJpaRepositorio;

public class MantenimientosRepositorioImpl implements MantenimientoRepositorioPuerto {

	private final IMantenimientosJpaRepositorio jpaRepositorio;
	private final IMantenimientosJpaMapper mapper;

	public MantenimientosRepositorioImpl(IMantenimientosJpaRepositorio jpaRepositorio, 
			IMantenimientosJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public Mantenimientos guardar(Mantenimientos mantenimiento) {
		MantenimientosJpa jpa = mapper.toEntity(mantenimiento);
		MantenimientosJpa saved = jpaRepositorio.save(jpa);
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<Mantenimientos> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<Mantenimientos> obtenerTodos() {
		return jpaRepositorio.findAllByOrderByFechaProgramadaDescIdMantenimientoDesc()
				.stream()
				.map(mapper::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public List<Mantenimientos> obtenerPorEquipo(int equipoId) {
		return jpaRepositorio.findByEquipoIdOrderByCreadoEnDesc(equipoId)
				.stream()
				.map(mapper::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Mantenimientos actualizar(Mantenimientos mantenimiento) {
		return mapper.toDomain(jpaRepositorio.save(mapper.toEntity(mantenimiento)));
	}

	@Override
	public void eliminar(int id) {
		jpaRepositorio.deleteById(id);
	}

	@Override
	public List<Mantenimientos> guardarTodos(List<Mantenimientos> mantenimientos) {
		return jpaRepositorio.saveAll(mantenimientos.stream().map(mapper::toEntity).toList()).stream()
				.map(mapper::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Integer obtenerMaxSecuenciaPorYear(int yearSnapshoted) {
		String maxSine = jpaRepositorio.findMaxSineSnapshotedByYear(yearSnapshoted);
		if (maxSine == null || maxSine.isBlank()) {
			return null;
		}

		int separador = maxSine.lastIndexOf('-');
		String secuencia = separador >= 0 ? maxSine.substring(separador + 1) : maxSine;
		try {
			return Integer.parseInt(secuencia);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	@Override
	public java.time.LocalDateTime obtenerUltimoCierrePorEquipo(int equipoId) {
		return jpaRepositorio.findTopByEquipoIdAndFecCierreNotNullOrderByFecCierreDesc(equipoId)
				.map(MantenimientosJpa::getFecCierre)
				.orElse(null);
	}
}
