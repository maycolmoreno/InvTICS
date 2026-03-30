package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerOrdenTrabajoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.ActividadChecklist;
import com.uisrael.gestionactivosapi.dominio.entidades.ActividadRealizada;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;
import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActividadChecklistRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActividadRealizadaRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodiasRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.MantenimientoRepositorioPuerto;
import com.uisrael.gestionactivosapi.presentacion.dto.response.OrdenActividadResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.OrdenTrabajoResponseDTO;

public class ObtenerOrdenTrabajoUseCaseImpl implements IObtenerOrdenTrabajoUseCase {

    private final MantenimientoRepositorioPuerto mantenimientoRepository;
    private final EquipoRepositorioPuerto equiposRepositorio;
    private final CustodiasRepositorioPuerto custodiasRepositorio;
    private final ActividadChecklistRepositorioPuerto actividadChecklistRepository;
    private final ActividadRealizadaRepositorioPuerto actividadRealizadaRepository;

    public ObtenerOrdenTrabajoUseCaseImpl(MantenimientoRepositorioPuerto mantenimientoRepository,
            EquipoRepositorioPuerto equiposRepositorio,
            CustodiasRepositorioPuerto custodiasRepositorio,
            ActividadChecklistRepositorioPuerto actividadChecklistRepository,
            ActividadRealizadaRepositorioPuerto actividadRealizadaRepository) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.equiposRepositorio = equiposRepositorio;
        this.custodiasRepositorio = custodiasRepositorio;
        this.actividadChecklistRepository = actividadChecklistRepository;
        this.actividadRealizadaRepository = actividadRealizadaRepository;
    }

    @Override
    public OrdenTrabajoResponseDTO obtener(Integer idMantenimiento) {
        Mantenimientos m = mantenimientoRepository.buscarPorId(idMantenimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mantenimiento no encontrado"));

        if (m.getEquipoId() == null) {
            throw new RecursoNoEncontradoException("Mantenimiento sin equipo asociado");
        }
        Equipos equipo = equiposRepositorio.buscarPorId(m.getEquipoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Equipo no encontrado"));

        Custodias custodia = custodiasRepositorio.buscarActivaPorEquipo(equipo.getIdEquipo())
                .orElse(null);

        LocalDate fechaUlt = null;
        if (m.getEquipoId() != null) {
            var ultimo = mantenimientoRepository.obtenerUltimoCierrePorEquipo(equipo.getIdEquipo());
            fechaUlt = ultimo != null ? ultimo.toLocalDate() : null;
        }
        Long dias = (fechaUlt != null) ? ChronoUnit.DAYS.between(fechaUlt, LocalDate.now()) : null;

        List<ActividadChecklist> checklist = actividadChecklistRepository.listarActivas();
        List<ActividadRealizada> realizadas = actividadRealizadaRepository
                .listarPorMantenimiento(idMantenimiento);

        Map<Integer, Boolean> realizadasMap = realizadas.stream()
                .collect(Collectors.toMap(ActividadRealizada::getIdActividad, ActividadRealizada::isRealizada, (a, b) -> a));

        List<OrdenActividadResponseDTO> actividades = checklist.stream().map(a -> {
            OrdenActividadResponseDTO dto = new OrdenActividadResponseDTO();
            dto.setIdActividad(a.getIdActividad());
            dto.setNombre(a.getNombre());
            dto.setCategoria(a.getCategoria());
            dto.setOrden(a.getOrden());
            dto.setRealizada(Boolean.TRUE.equals(realizadasMap.get(a.getIdActividad())));
            return dto;
        }).toList();

        OrdenTrabajoResponseDTO resp = new OrdenTrabajoResponseDTO();
        resp.setIdMantenimiento(m.getIdMantenimiento());
        resp.setSineSnapshoted(m.getSineSnapshoted());
        resp.setCreadoEn(m.getCreadoEn());
        resp.setEstadoInterno(m.getEstadoInterno() != null ? m.getEstadoInterno().name() : null);
        resp.setTipoMantenimiento(m.getTipoMantenimiento());
        resp.setPrioridad(m.getEstado());

        resp.setIdEquipo(equipo.getIdEquipo());
        resp.setSerial(equipo.getSerial());
        resp.setMarca(equipo.getFkMarca() != null ? equipo.getFkMarca().getNombre() : "");
        resp.setModelo(equipo.getModelo());
        resp.setTipoEquipo(equipo.getTipoEquipo());
        resp.setCodigoSap(equipo.getCodigoSap());

        if (custodia != null && custodia.getFkCustodio() != null) {
            resp.setCustodioNombre(custodia.getFkCustodio().getNombre());
        }
        if (custodia != null && custodia.getFkUbicacion() != null) {
            resp.setUbicacionNombre(custodia.getFkUbicacion().getNombre());
        }

        resp.setFechaUltimoMantenimiento(fechaUlt);
        resp.setDiasSinMantenimiento(dias);
        resp.setActividades(actividades);

        return resp;
    }
}
