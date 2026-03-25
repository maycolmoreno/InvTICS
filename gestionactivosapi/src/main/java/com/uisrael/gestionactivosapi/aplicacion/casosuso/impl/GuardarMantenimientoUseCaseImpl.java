package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IGuardarMantenimientoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.ActividadRealizada;
import com.uisrael.gestionactivosapi.dominio.entidades.EstadoInternoMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActividadRealizadaRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.MantenimientoRepositorioPuerto;
import com.uisrael.gestionactivosapi.presentacion.dto.request.ActividadRealizadaRequestDTO;

public class GuardarMantenimientoUseCaseImpl implements IGuardarMantenimientoUseCase {

    private final MantenimientoRepositorioPuerto mantenimientoRepositorio;
    private final ActividadRealizadaRepositorioPuerto actividadRealizadaRepositorio;

    public GuardarMantenimientoUseCaseImpl(MantenimientoRepositorioPuerto mantenimientoRepositorio,
            ActividadRealizadaRepositorioPuerto actividadRealizadaRepositorio) {
        this.mantenimientoRepositorio = mantenimientoRepositorio;
        this.actividadRealizadaRepositorio = actividadRealizadaRepositorio;
    }

    @Override
    public void guardar(Integer idMantenimiento, List<ActividadRealizadaRequestDTO> actividades,
            String observaciones, String estadoGeneral, String firmaBase64) {
        if (idMantenimiento == null || idMantenimiento <= 0) {
            throw new IllegalArgumentException("idMantenimiento es obligatorio");
        }

        Mantenimientos mantenimiento = mantenimientoRepositorio.buscarPorId(idMantenimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mantenimiento no encontrado"));

        actividadRealizadaRepositorio.eliminarPorMantenimiento(idMantenimiento);

        if (actividades != null && !actividades.isEmpty()) {
            List<ActividadRealizada> entidades = actividades.stream().filter(a -> a != null).map(a -> {
                ActividadRealizada ar = new ActividadRealizada();
                ar.setIdMantenimiento(idMantenimiento);
                ar.setIdActividad(a.getIdActividad());
                ar.setRealizada(Boolean.TRUE.equals(a.getRealizada()));
                return ar;
            }).toList();
            actividadRealizadaRepositorio.guardarTodas(entidades);
        }

        StringBuilder desc = new StringBuilder();
        if (estadoGeneral != null && !estadoGeneral.isBlank()) {
            desc.append("Estado general: ").append(estadoGeneral.trim());
        }
        if (observaciones != null && !observaciones.isBlank()) {
            if (!desc.isEmpty()) {
                desc.append("\n");
            }
            desc.append("Observaciones: ").append(observaciones.trim());
        }
        if (firmaBase64 != null && !firmaBase64.isBlank()) {
            if (!desc.isEmpty()) {
                desc.append("\n");
            }
            desc.append("Firma tecnico: capturada");
        }

        mantenimiento.setDescripcion(desc.toString());
        mantenimiento.setEstadoInterno(EstadoInternoMantenimiento.CERRADO);
        mantenimiento.setFecCierre(LocalDateTime.now());

        mantenimientoRepositorio.guardar(mantenimiento);
    }
}
