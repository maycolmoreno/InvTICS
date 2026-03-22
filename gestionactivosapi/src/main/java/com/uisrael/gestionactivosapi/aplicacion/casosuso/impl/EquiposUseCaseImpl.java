package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IEquiposUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.DuplicidadException;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;
import com.uisrael.gestionactivosapi.dominio.repositorios.IEquiposRepositorio;

public class EquiposUseCaseImpl implements IEquiposUseCase {

	private static final String CODIGO_ACTIVO_FIJO_REGEX = "^[A-Z]_EC_\\d{11}$";

	private final IEquiposRepositorio repositorio;

	public EquiposUseCaseImpl(IEquiposRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Equipos crear(Equipos equipo) {
		if (equipo.getCodigoSap() != null && !equipo.getCodigoSap().isBlank()) {
			String codigoActivoFijo = equipo.getCodigoSap().trim().toUpperCase();
			if (!codigoActivoFijo.matches(CODIGO_ACTIVO_FIJO_REGEX)) {
				throw new IllegalArgumentException("El Código Activo Fijo debe tener el formato A_EC_00000000919");
			}
			if (repositorio.existeCodigo(codigoActivoFijo)) {
				throw new DuplicidadException("Ya existe un equipo con ese Código Activo Fijo");
			}
		}
		if (repositorio.existeSerial(equipo.getSerial().trim())) {
			throw new DuplicidadException("Ya existe un equipo con ese Serial");
		}

		if (equipo.getIp() != null && !equipo.getIp().isBlank()) {
			String ip = equipo.getIp().trim();

			if (repositorio.existeIP(ip)) {
				throw new DuplicidadException("Ya existe un equipo con esa dirección IP");
			}

		}

		if (equipo.getMac() != null && !equipo.getMac().isBlank()) {
			String mac = equipo.getMac().trim().toUpperCase();

			if (repositorio.existeMAC(mac)) {
				throw new DuplicidadException("Ya existe un equipo con esa dirección MAC");
			}

		}

		return repositorio.guardar(equipo);
	}

	@Override
	public Equipos obtenerPorId(int id) {
		return repositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Equipo no encontrado"));
	}

	@Override
	public List<Equipos> listar() {
		return repositorio.listarTodos();
	}

	@Override
	public Equipos actualizar(int id, Equipos equipo) {
		if (equipo.getCodigoSap() != null && !equipo.getCodigoSap().isBlank()) {
			String codigoActivoFijo = equipo.getCodigoSap().trim().toUpperCase();
			if (!codigoActivoFijo.matches(CODIGO_ACTIVO_FIJO_REGEX)) {
				throw new IllegalArgumentException("El Código Activo Fijo debe tener el formato A_EC_00000000919");
			}
			if (repositorio.existeCodigoParaOtro(codigoActivoFijo, id)) {
				throw new DuplicidadException("Ya existe un equipo con ese Código Activo Fijo");
			}
		}

		if (repositorio.existeSerialParaOtro(equipo.getSerial().trim(), id)) {
			throw new DuplicidadException("Ya existe un equipo con ese Serial");
		}

		if (equipo.getIp() != null && !equipo.getIp().isBlank()) {
			if (repositorio.existeIPParaOtro(equipo.getIp().trim(), id)) {
				throw new DuplicidadException("Ya existe un equipo con esa dirección IP");
			}
		}

		if (equipo.getMac() != null && !equipo.getMac().isBlank()) {
			if (repositorio.existeMACParaOtro(equipo.getMac().trim(), id)) {
				throw new DuplicidadException("Ya existe un equipo con esa dirección MAC");
			}
		}

		Equipos actualizado = new Equipos(id, equipo.getCodigoSap(), equipo.getTipoEquipo(), equipo.getModelo(),
				equipo.getSerial(), equipo.getProcesador(), equipo.getMemoriaRamGb(),
				equipo.getCapacidadAlmacenamientoGb(), equipo.getSistemaOperativo(),
				equipo.getLicenciaWindowsActivada(), equipo.getEtiquetaActivoFijo(), equipo.getTipoLicenciaOffice(),
				equipo.getVersionOffice(), equipo.getUnionDominio(), equipo.getIp(), equipo.getMac(),
				equipo.getFechaCompra(), equipo.getPrecioCompra(), equipo.getEstadoEquipo(),
				equipo.getObservacionEquipo(), equipo.isEstado(), equipo.getFkMarca(), equipo.getFkCategoria());

		return repositorio.actualizar(id, actualizado);
	}

	@Override
	public Equipos actualizarEstado(int id, boolean estado) {

		Equipos equipo = repositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Equipo no encontrado"));

		Equipos actualizado = new Equipos(id, equipo.getCodigoSap(), equipo.getTipoEquipo(), equipo.getModelo(),
				equipo.getSerial(), equipo.getProcesador(), equipo.getMemoriaRamGb(),
				equipo.getCapacidadAlmacenamientoGb(), equipo.getSistemaOperativo(),
				equipo.getLicenciaWindowsActivada(), equipo.getEtiquetaActivoFijo(), equipo.getTipoLicenciaOffice(),
				equipo.getVersionOffice(), equipo.getUnionDominio(), equipo.getIp(), equipo.getMac(),
				equipo.getFechaCompra(), equipo.getPrecioCompra(), equipo.getEstadoEquipo(),
				equipo.getObservacionEquipo(), estado, equipo.getFkMarca(), equipo.getFkCategoria());

		return repositorio.actualizar(id, actualizado);
	}

	@Override
	public boolean existeCodigo(String codigo) {
		return repositorio.existeCodigo(codigo.trim());
	}

	@Override
	public boolean existeCodigoParaOtro(String codigo, int idEquipo) {
		return repositorio.existeCodigoParaOtro(codigo.trim(), idEquipo);
	}

	@Override
	public boolean existeSerial(String serial) {
		return repositorio.existeSerial(serial.trim());
	}

	@Override
	public boolean existeSerialParaOtro(String serial, int idEquipo) {
		return repositorio.existeSerialParaOtro(serial.trim(), idEquipo);
	}

	@Override
	public boolean existeIP(String ip) {
		return repositorio.existeIP(ip.trim());
	}

	@Override
	public boolean existeIPParaOtro(String ip, int idEquipo) {
		return repositorio.existeIPParaOtro(ip.trim(), idEquipo);
	}

	@Override
	public boolean existeMAC(String mac) {
		return repositorio.existeMAC(mac.trim());
	}

	@Override
	public boolean existeMACParaOtro(String mac, int idEquipo) {
		return repositorio.existeMACParaOtro(mac.trim(), idEquipo);
	}

}
