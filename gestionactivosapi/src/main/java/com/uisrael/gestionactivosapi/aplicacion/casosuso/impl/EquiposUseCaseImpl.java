package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IEquiposUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.DuplicidadException;
import com.uisrael.gestionactivosapi.dominio.modelo.Pagina;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoRepositorioPuerto;

public class EquiposUseCaseImpl implements IEquiposUseCase {

	private static final String CODIGO_ACTIVO_FIJO_REGEX = "^[A-Z]_EC_\\d{11}$";

	private final EquipoRepositorioPuerto equipoRepositorio;

	public EquiposUseCaseImpl(EquipoRepositorioPuerto equipoRepositorio) {
		this.equipoRepositorio = equipoRepositorio;
	}

	@Override
	public Equipos crear(Equipos equipo) {
		if (equipo.getCodigoSap() != null && !equipo.getCodigoSap().isBlank()) {
			String codigoActivoFijo = equipo.getCodigoSap().trim().toUpperCase();
			if (!codigoActivoFijo.matches(CODIGO_ACTIVO_FIJO_REGEX)) {
				throw new IllegalArgumentException("El Código Activo Fijo debe tener el formato A_EC_00000000919");
			}
			if (equipoRepositorio.existeCodigo(codigoActivoFijo)) {
				throw new DuplicidadException("Ya existe un equipo con ese Código Activo Fijo");
			}
		}
		if (equipoRepositorio.existeSerial(equipo.getSerial().trim())) {
			throw new DuplicidadException("Ya existe un equipo con ese Serial");
		}

		if (equipo.getIp() != null && !equipo.getIp().isBlank()) {
			String ip = equipo.getIp().trim();

			if (equipoRepositorio.existeIP(ip)) {
				throw new DuplicidadException("Ya existe un equipo con esa dirección IP");
			}

		}

		if (equipo.getMac() != null && !equipo.getMac().isBlank()) {
			String mac = equipo.getMac().trim().toUpperCase();

			if (equipoRepositorio.existeMAC(mac)) {
				throw new DuplicidadException("Ya existe un equipo con esa dirección MAC");
			}

		}

		return equipoRepositorio.guardar(equipo);
	}

	@Override
	public Equipos obtenerPorId(int id) {
		return equipoRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Equipo no encontrado"));
	}

	@Override
	public List<Equipos> listar() {
		return equipoRepositorio.listarTodos();
	}

	@Override
	public Pagina<Equipos> listarPaginado(int pagina, int tamanio) {
		return equipoRepositorio.listarPaginado(pagina, tamanio);
	}

	@Override
	public Equipos actualizar(int id, Equipos equipo) {
		if (equipo.getCodigoSap() != null && !equipo.getCodigoSap().isBlank()) {
			String codigoActivoFijo = equipo.getCodigoSap().trim().toUpperCase();
			if (!codigoActivoFijo.matches(CODIGO_ACTIVO_FIJO_REGEX)) {
				throw new IllegalArgumentException("El Código Activo Fijo debe tener el formato A_EC_00000000919");
			}
			if (equipoRepositorio.existeCodigoParaOtro(codigoActivoFijo, id)) {
				throw new DuplicidadException("Ya existe un equipo con ese Código Activo Fijo");
			}
		}

		if (equipoRepositorio.existeSerialParaOtro(equipo.getSerial().trim(), id)) {
			throw new DuplicidadException("Ya existe un equipo con ese Serial");
		}

		if (equipo.getIp() != null && !equipo.getIp().isBlank()) {
			if (equipoRepositorio.existeIPParaOtro(equipo.getIp().trim(), id)) {
				throw new DuplicidadException("Ya existe un equipo con esa dirección IP");
			}
		}

		if (equipo.getMac() != null && !equipo.getMac().isBlank()) {
			if (equipoRepositorio.existeMACParaOtro(equipo.getMac().trim(), id)) {
				throw new DuplicidadException("Ya existe un equipo con esa dirección MAC");
			}
		}

		Equipos actualizado = new Equipos(id, equipo.getCodigoSap(), equipo.getTipoEquipo(), equipo.getModelo(),
				equipo.getSerial(), equipo.getProcesador(), equipo.getMemoriaRamGb(),
				equipo.getCapacidadAlmacenamientoGb(), equipo.getSistemaOperativo(),
				equipo.getLicenciaWindowsActivada(), equipo.getEtiquetaActivoFijo(), equipo.getTipoLicenciaOffice(),
				equipo.getVersionOffice(), equipo.getUnionDominio(), equipo.getIp(), equipo.getMac(),
				equipo.getFechaCompra(), equipo.getPrecioCompra(), equipo.getEstadoEquipo(),
				equipo.getObservacionEquipo(), equipo.isEstado(), equipo.getFkMarca(), equipo.getFkCategoria(), equipo.getFkUbicacion());

		return equipoRepositorio.actualizar(id, actualizado);
	}

	@Override
	public Equipos actualizarEstado(int id, boolean estado) {

		Equipos equipo = equipoRepositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Equipo no encontrado"));

		Equipos actualizado = new Equipos(id, equipo.getCodigoSap(), equipo.getTipoEquipo(), equipo.getModelo(),
				equipo.getSerial(), equipo.getProcesador(), equipo.getMemoriaRamGb(),
				equipo.getCapacidadAlmacenamientoGb(), equipo.getSistemaOperativo(),
				equipo.getLicenciaWindowsActivada(), equipo.getEtiquetaActivoFijo(), equipo.getTipoLicenciaOffice(),
				equipo.getVersionOffice(), equipo.getUnionDominio(), equipo.getIp(), equipo.getMac(),
				equipo.getFechaCompra(), equipo.getPrecioCompra(), equipo.getEstadoEquipo(),
				equipo.getObservacionEquipo(), estado, equipo.getFkMarca(), equipo.getFkCategoria(), equipo.getFkUbicacion());

		return equipoRepositorio.actualizar(id, actualizado);
	}

	@Override
	public boolean existeCodigo(String codigo) {
		return equipoRepositorio.existeCodigo(codigo.trim());
	}

	@Override
	public boolean existeCodigoParaOtro(String codigo, int idEquipo) {
		return equipoRepositorio.existeCodigoParaOtro(codigo.trim(), idEquipo);
	}

	@Override
	public boolean existeSerial(String serial) {
		return equipoRepositorio.existeSerial(serial.trim());
	}

	@Override
	public boolean existeSerialParaOtro(String serial, int idEquipo) {
		return equipoRepositorio.existeSerialParaOtro(serial.trim(), idEquipo);
	}

	@Override
	public boolean existeIP(String ip) {
		return equipoRepositorio.existeIP(ip.trim());
	}

	@Override
	public boolean existeIPParaOtro(String ip, int idEquipo) {
		return equipoRepositorio.existeIPParaOtro(ip.trim(), idEquipo);
	}

	@Override
	public boolean existeMAC(String mac) {
		return equipoRepositorio.existeMAC(mac.trim());
	}

	@Override
	public boolean existeMACParaOtro(String mac, int idEquipo) {
		return equipoRepositorio.existeMACParaOtro(mac.trim(), idEquipo);
	}

}
