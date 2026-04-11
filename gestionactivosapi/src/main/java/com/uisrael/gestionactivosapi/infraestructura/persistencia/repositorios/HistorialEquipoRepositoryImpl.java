package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.dto.EstadisticasEquipoDTO;
import com.uisrael.gestionactivosapi.dominio.dto.HistorialEquipoDTO;
import com.uisrael.gestionactivosapi.dominio.dto.MantenimientoHistorialDTO;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;
import com.uisrael.gestionactivosapi.dominio.entidades.EstadoInternoMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodiasRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.HistorialEquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.MantenimientoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UsuarioRepositorioPuerto;

public class HistorialEquipoRepositoryImpl implements HistorialEquipoRepositorioPuerto {

	private final EquipoRepositorioPuerto equiposRepositorio;
	private final CustodiasRepositorioPuerto custodiasRepositorio;
	private final MantenimientoRepositorioPuerto mantenimientosRepositorio;
	private final UsuarioRepositorioPuerto usuariosRepositorio;

	public HistorialEquipoRepositoryImpl(EquipoRepositorioPuerto equiposRepositorio,
			CustodiasRepositorioPuerto custodiasRepositorio,
			MantenimientoRepositorioPuerto mantenimientosRepositorio,
			UsuarioRepositorioPuerto usuariosRepositorio) {
		this.equiposRepositorio = equiposRepositorio;
		this.custodiasRepositorio = custodiasRepositorio;
		this.mantenimientosRepositorio = mantenimientosRepositorio;
		this.usuariosRepositorio = usuariosRepositorio;
	}

	@Override
	public HistorialEquipoDTO findHistorialByEquipoId(Long equipoId) {
		Equipos equipo = obtenerEquipo(equipoId);
		Custodias custodia = custodiasRepositorio.buscarActivaPorEquipo(equipo.getIdEquipo()).orElse(null);

		HistorialEquipoDTO dto = new HistorialEquipoDTO();
		dto.setIdEquipo(equipo.getIdEquipo());
		dto.setMarca(equipo.getFkMarca() != null ? equipo.getFkMarca().getNombre() : null);
		dto.setModelo(equipo.getModelo());
		dto.setSerial(equipo.getSerial());
		dto.setCodigoSap(equipo.getCodigoSap());
		dto.setFechaCompra(equipo.getFechaCompra());
		dto.setEstadoEquipo(equipo.getEstadoEquipo());
		dto.setProcesador(equipo.getProcesador());
		dto.setMemoriaRamGb(equipo.getMemoriaRamGb());
		dto.setCapacidadAlmacenamientoGb(equipo.getCapacidadAlmacenamientoGb());
		dto.setLicenciaWindowsActivada(equipo.getLicenciaWindowsActivada());
		dto.setCategoriaNombre(equipo.getFkCategoria() != null ? equipo.getFkCategoria().getNombre() : null);

		if (custodia != null) {
			dto.setFechaInicioCustodio(custodia.getFechaInicio());
			if (custodia.getFkCustodio() != null) {
				dto.setCustodioNombre(custodia.getFkCustodio().getNombre());
				dto.setDepartamentoNombre(custodia.getFkCustodio().getFkDepartamento() != null
						? custodia.getFkCustodio().getFkDepartamento().getNombre()
						: null);
			}
			if (custodia.getFkUbicacion() != null) {
				dto.setUbicacionNombre(custodia.getFkUbicacion().getNombre());
				dto.setUbicacionCiudad(custodia.getFkUbicacion().getCiudad());
			}
		}

		return dto;
	}

	@Override
	public List<MantenimientoHistorialDTO> findMantenimientosByEquipoId(Long equipoId) {
		return obtenerMantenimientos(equipoId).stream()
				.sorted(Comparator.comparing(Mantenimientos::getCreadoEn, Comparator.nullsLast(Comparator.reverseOrder())))
				.map(this::toHistorial)
				.toList();
	}

	@Override
	public EstadisticasEquipoDTO calcularEstadisticas(Long equipoId) {
		List<Mantenimientos> mantenimientos = obtenerMantenimientos(equipoId);

		EstadisticasEquipoDTO dto = new EstadisticasEquipoDTO();
		dto.setTotalMantenimientos(mantenimientos.size());
		dto.setTotalCerrados((int) mantenimientos.stream()
				.filter(m -> m.getEstadoInterno() == EstadoInternoMantenimiento.CERRADO)
				.count());
		dto.setTotalEnProceso((int) mantenimientos.stream()
				.filter(m -> m.getEstadoInterno() == EstadoInternoMantenimiento.EN_PROCESO)
				.count());
		dto.setDiasSinMantenimiento(calcularDiasSinMantenimiento(mantenimientos));
		dto.setPromedioDiasEntreMantenimientos(calcularPromedioDiasEntreCierres(mantenimientos));
		dto.setMantsPorAnio(calcularMantenimientosPorAnio(mantenimientos));
		return dto;
	}

	private Equipos obtenerEquipo(Long equipoId) {
		if (equipoId == null) {
			throw new IllegalArgumentException("El equipo es obligatorio");
		}
		return equiposRepositorio.buscarPorId(equipoId.intValue())
				.orElseThrow(() -> new RecursoNoEncontradoException("Equipo no encontrado"));
	}

	private List<Mantenimientos> obtenerMantenimientos(Long equipoId) {
		return mantenimientosRepositorio.obtenerPorEquipo(obtenerEquipo(equipoId).getIdEquipo());
	}

	private MantenimientoHistorialDTO toHistorial(Mantenimientos mantenimiento) {
		MantenimientoHistorialDTO dto = new MantenimientoHistorialDTO();
		dto.setIdMantenimiento(mantenimiento.getIdMantenimiento());
		dto.setSineSnapshoted(mantenimiento.getSineSnapshoted());
		dto.setEstadoInterno(mantenimiento.getEstadoInterno() != null ? mantenimiento.getEstadoInterno().name() : null);
		dto.setDescripcion(mantenimiento.getDescripcion());
		dto.setFechaCierre(mantenimiento.getFecCierre());
		dto.setTecnicoNombre(mantenimiento.getIdUsuario() != null
				? usuariosRepositorio.buscarPorId(mantenimiento.getIdUsuario()).map(usuario -> usuario.getNombre())
						.orElse(null)
				: null);
		dto.setTipoInferido(inferirTipo(mantenimiento));
		return dto;
	}

	private String inferirTipo(Mantenimientos mantenimiento) {
		if (mantenimiento.getTipoMantenimiento() != null && !mantenimiento.getTipoMantenimiento().isBlank()) {
			return mantenimiento.getTipoMantenimiento();
		}
		if (mantenimiento.getTipoOrigen() != null) {
			return mantenimiento.getTipoOrigen().name();
		}
		return "NO_DEFINIDO";
	}

	private Long calcularDiasSinMantenimiento(List<Mantenimientos> mantenimientos) {
		LocalDate ultimoCierre = mantenimientos.stream()
				.map(Mantenimientos::getFecCierre)
				.filter(Objects::nonNull)
				.max(LocalDateTime::compareTo)
				.map(LocalDateTime::toLocalDate)
				.orElse(null);
		return ultimoCierre == null ? null : ChronoUnit.DAYS.between(ultimoCierre, LocalDate.now());
	}

	private Double calcularPromedioDiasEntreCierres(List<Mantenimientos> mantenimientos) {
		List<LocalDate> cierres = mantenimientos.stream()
				.map(Mantenimientos::getFecCierre)
				.filter(Objects::nonNull)
				.map(LocalDateTime::toLocalDate)
				.sorted()
				.toList();
		if (cierres.size() < 2) {
			return null;
		}

		long totalDias = 0L;
		for (int i = 1; i < cierres.size(); i++) {
			totalDias += ChronoUnit.DAYS.between(cierres.get(i - 1), cierres.get(i));
		}
		return totalDias / (double) (cierres.size() - 1);
	}

	private Map<Integer, Long> calcularMantenimientosPorAnio(List<Mantenimientos> mantenimientos) {
		return mantenimientos.stream()
				.map(mantenimiento -> mantenimiento.getFechaProgramada() != null
						? mantenimiento.getFechaProgramada().getYear()
						: mantenimiento.getCreadoEn() != null ? mantenimiento.getCreadoEn().getYear() : null)
				.filter(Objects::nonNull)
				.sorted()
				.collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
	}
}
