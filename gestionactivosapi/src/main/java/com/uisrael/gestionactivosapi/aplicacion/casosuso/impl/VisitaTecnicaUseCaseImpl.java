package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IVisitaTecnicaUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.EquipoVisita;
import com.uisrael.gestionactivosapi.dominio.entidades.EstadoMantenimientoVisita;
import com.uisrael.gestionactivosapi.dominio.repositorios.IEquipoVisitaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.response.VisitaCustodioResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.VisitaEquipoResponseDTO;

@Service
public class VisitaTecnicaUseCaseImpl implements IVisitaTecnicaUseCase {

    private final IEquipoVisitaRepositorio equipoVisitaRepositorio;

    public VisitaTecnicaUseCaseImpl(IEquipoVisitaRepositorio equipoVisitaRepositorio) {
        this.equipoVisitaRepositorio = equipoVisitaRepositorio;
    }

    @Override
    public List<VisitaEquipoResponseDTO> obtenerEquipos(Long ubicacionId, Long custodioId) {
        List<EquipoVisita> equipos = equipoVisitaRepositorio.findEquiposByUbicacionAndCustodio(ubicacionId, custodioId);
        if (equipos == null) {
            return List.of();
        }
        LocalDate hoy = LocalDate.now();

        return equipos.stream().map(e -> {
            LocalDate fechaUlt = e.getFechaUltimoMantenimiento() != null
                    ? e.getFechaUltimoMantenimiento().toLocalDate()
                    : null;
            Long dias = (fechaUlt != null) ? ChronoUnit.DAYS.between(fechaUlt, hoy) : null;
            EstadoMantenimientoVisita estado = calcularEstado(dias);

            VisitaEquipoResponseDTO dto = new VisitaEquipoResponseDTO();
            dto.setIdEquipo(e.getIdEquipo());
            dto.setSerial(e.getSerial());
            dto.setMarca(e.getMarca());
            dto.setModelo(e.getModelo());
            dto.setTipoEquipo(e.getTipoEquipo());
            dto.setCodigoSap(e.getCodigoSap());
            dto.setCustodioNombre(e.getCustodioNombre());
            dto.setCustodioArea(e.getCustodioArea());
            dto.setUbicacionNombre(e.getUbicacionNombre());
            dto.setFechaUltimoMantenimiento(fechaUlt);
            dto.setDiasSinMantenimiento(dias);
            dto.setEstadoMantenimiento(estado.name());
            return dto;
        }).toList();
    }

    @Override
    public List<VisitaCustodioResponseDTO> obtenerCustodios(Long ubicacionId) {
        List<EquipoVisita> equipos = equipoVisitaRepositorio.findEquiposByUbicacionAndCustodio(ubicacionId, null);
        if (equipos == null || equipos.isEmpty()) {
            return List.of();
        }

        Map<Integer, VisitaCustodioResponseDTO> unicos = equipos.stream()
                .filter(e -> e.getIdCustodio() > 0)
                .map(e -> {
                    VisitaCustodioResponseDTO dto = new VisitaCustodioResponseDTO();
                    dto.setIdCustodio(e.getIdCustodio());
                    dto.setNombre(e.getCustodioNombre());
                    dto.setArea(e.getCustodioArea() != null ? e.getCustodioArea() : "");
                    return dto;
                })
                .collect(java.util.stream.Collectors.toMap(
                        VisitaCustodioResponseDTO::getIdCustodio,
                        dto -> dto,
                        (a, b) -> a));

        return unicos.values().stream()
                .sorted(Comparator.comparing(
                        dto -> dto.getNombre() != null ? dto.getNombre() : "",
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private EstadoMantenimientoVisita calcularEstado(Long dias) {
        if (dias == null) {
            return EstadoMantenimientoVisita.URGENTE;
        }
        if (dias == 0) {
            return EstadoMantenimientoVisita.REVISADO;
        }
        if (dias > 180) {
            return EstadoMantenimientoVisita.URGENTE;
        }
        if (dias >= 90) {
            return EstadoMantenimientoVisita.PROXIMO;
        }
        return EstadoMantenimientoVisita.AL_DIA;
    }
}
