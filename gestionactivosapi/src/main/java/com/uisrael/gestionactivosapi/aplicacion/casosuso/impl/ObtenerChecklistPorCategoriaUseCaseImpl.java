package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerChecklistPorCategoriaUseCase;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.ActividadChecklist;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActividadChecklistRepositorioPuerto;

public class ObtenerChecklistPorCategoriaUseCaseImpl implements IObtenerChecklistPorCategoriaUseCase {

    private final ActividadChecklistRepositorioPuerto actividadChecklistRepository;

    public ObtenerChecklistPorCategoriaUseCaseImpl(ActividadChecklistRepositorioPuerto actividadChecklistRepository) {
        this.actividadChecklistRepository = actividadChecklistRepository;
    }

    @Override
    public List<ActividadChecklist> listarActivas() {
        return actividadChecklistRepository.listarActivas();
    }

    @Override
    public ActividadChecklist obtenerPorId(Integer id) {
        return actividadChecklistRepository.obtenerPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad checklist no encontrada con id: " + id));
    }

    @Override
    public List<ActividadChecklist> ejecutar(Integer idCategoria) {
        if (idCategoria == null) {
            throw new IllegalArgumentException("La categoria es obligatoria");
        }
        return actividadChecklistRepository.listarActivasPorCategoria(idCategoria);
    }

    @Override
    public ActividadChecklist crear(ActividadChecklist actividad) {
        if (actividad.getNombre() == null || actividad.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la actividad es obligatorio");
        }
        if (actividad.getCategoria() == null || actividad.getCategoria().isBlank()) {
            throw new IllegalArgumentException("La categoria es obligatoria");
        }
        actividad.setEstado(true);
        return actividadChecklistRepository.guardar(actividad);
    }

    @Override
    public ActividadChecklist actualizar(Integer id, ActividadChecklist actividad) {
        actividadChecklistRepository.obtenerPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad checklist no encontrada"));
        actividad.setIdActividad(id);
        return actividadChecklistRepository.guardar(actividad);
    }

    @Override
    public void eliminar(Integer id) {
        actividadChecklistRepository.obtenerPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad checklist no encontrada"));
        actividadChecklistRepository.eliminar(id);
    }
}
