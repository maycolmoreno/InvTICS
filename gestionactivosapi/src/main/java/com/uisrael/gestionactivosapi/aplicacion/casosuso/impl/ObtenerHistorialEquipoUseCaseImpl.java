package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerHistorialEquipoUseCase;
import com.uisrael.gestionactivosapi.dominio.dto.EstadisticasEquipoDTO;
import com.uisrael.gestionactivosapi.dominio.dto.HistorialCompletoDTO;
import com.uisrael.gestionactivosapi.dominio.dto.HistorialEquipoDTO;
import com.uisrael.gestionactivosapi.dominio.dto.MantenimientoHistorialDTO;
import com.uisrael.gestionactivosapi.dominio.entidades.EstadoMantenimientoVisita;
import com.uisrael.gestionactivosapi.dominio.repositorios.IHistorialEquipoRepository;

import java.util.List;

public class ObtenerHistorialEquipoUseCaseImpl implements IObtenerHistorialEquipoUseCase {

    private final IHistorialEquipoRepository historialRepositorio;

    public ObtenerHistorialEquipoUseCaseImpl(IHistorialEquipoRepository historialRepositorio) {
        this.historialRepositorio = historialRepositorio;
    }

    @Override
    public HistorialCompletoDTO obtener(Long equipoId) {
        HistorialEquipoDTO equipo = historialRepositorio.findHistorialByEquipoId(equipoId);
        List<MantenimientoHistorialDTO> mantenimientos = historialRepositorio.findMantenimientosByEquipoId(equipoId);
        EstadisticasEquipoDTO stats = historialRepositorio.calcularEstadisticas(equipoId);

        HistorialCompletoDTO completo = new HistorialCompletoDTO();
        completo.setEquipo(equipo);
        completo.setMantenimientos(mantenimientos);
        completo.setEstadisticas(stats);
        completo.setEstadoMantenimiento(calcularEstado(stats.getDiasSinMantenimiento()).name());
        return completo;
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
