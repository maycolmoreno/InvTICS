package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IEquiposJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;

public class EquiposRepositorioImpl implements EquipoRepositorioPuerto {

	private final IEquiposJpaRepositorio jpaRepositorio;
	private final IEquiposJpaMapper mapper;

	public EquiposRepositorioImpl(IEquiposJpaRepositorio jpaRepositorio, IEquiposJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public Equipos guardar(Equipos equipo) {
		EquiposJpa jpa = mapper.toEntity(equipo);
		EquiposJpa saved = jpaRepositorio.save(jpa);
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<Equipos> obtenerPorId(Integer id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<Equipos> obtenerTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Equipos actualizar(int id, Equipos equipo) {
		EquiposJpa jpa = mapper.toEntity(equipo);
		EquiposJpa updated = jpaRepositorio.save(jpa);
		return mapper.toDomain(updated);
	}

	@Override
	public void actualizar(Equipos equipo) {
		jpaRepositorio.save(mapper.toEntity(equipo));
	}

	@Override
	public void eliminar(Integer id) {
		jpaRepositorio.deleteById(id);
	}

	@Override
	public Equipos actualizarEstado(int id, boolean estado) {
		Optional<EquiposJpa> optional = jpaRepositorio.findById(id);
		if (optional.isPresent()) {
			EquiposJpa jpa = optional.get();
			jpa.setEstado(estado);
			EquiposJpa updated = jpaRepositorio.save(jpa);
			return mapper.toDomain(updated);
		}
		return null;
	}

	@Override
	public boolean existeCodigo(String codigo) {
		return jpaRepositorio.existsByCodigoSapIgnoreCase(codigo);
	}

	@Override
	public boolean existeCodigoParaOtro(String codigo, int idEquipo) {
		return jpaRepositorio.existsByCodigoSapIgnoreCaseAndIdEquipoNot(codigo, idEquipo);
	}

	@Override
	public boolean existeSerial(String serial) {
		return jpaRepositorio.existsBySerialIgnoreCase(serial);
	}

	@Override
	public boolean existeSerialParaOtro(String serial, int idEquipo) {
		return jpaRepositorio.existsBySerialIgnoreCaseAndIdEquipoNot(serial, idEquipo);
	}

	@Override
	public boolean existeIP(String ip) {
		return jpaRepositorio.existsByIpIgnoreCase(ip);
	}

	@Override
	public boolean existeIPParaOtro(String ip, int idEquipo) {
		return jpaRepositorio.existsByIpIgnoreCaseAndIdEquipoNot(ip, idEquipo);
	}

	@Override
	public boolean existeMAC(String mac) {
		return jpaRepositorio.existsByMacIgnoreCase(mac);
	}

	@Override
	public boolean existeMACParaOtro(String mac, int idEquipo) {
		return jpaRepositorio.existsByMacIgnoreCaseAndIdEquipoNot(mac, idEquipo);
	}

	@Override
	public List<Equipos> obtenerPorEstado(boolean estado) {
		return obtenerTodos().stream().filter(equipo -> equipo.isEstado() == estado).collect(Collectors.toList());
	}

	@Override
	public List<Equipos> obtenerPorCustodio(Integer custodioId) {
		return List.of();
	}

	@Override
	public List<Equipos> obtenerPorCategoria(Integer categoriaId) {
		return obtenerTodos().stream()
				.filter(equipo -> equipo.getFkCategoria() != null
						&& equipo.getFkCategoria().getIdCategoria() == categoriaId)
				.collect(Collectors.toList());
	}
}
