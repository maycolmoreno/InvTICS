package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerChecklistPorCategoriaUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.ActividadChecklist;
import com.uisrael.gestionactivosapi.dominio.repositorios.IActividadChecklistRepository;

public class ObtenerChecklistPorCategoriaUseCaseImpl implements IObtenerChecklistPorCategoriaUseCase {

    private final IActividadChecklistRepository actividadChecklistRepository;

    public ObtenerChecklistPorCategoriaUseCaseImpl(IActividadChecklistRepository actividadChecklistRepository) {
        this.actividadChecklistRepository = actividadChecklistRepository;
    }

    @Override
    public List<ActividadChecklist> ejecutar(Integer idCategoria) {
        if (idCategoria == null) {
            throw new IllegalArgumentException("La categoria es obligatoria");
        }
        return actividadChecklistRepository.listarActivasPorCategoria(idCategoria);
    }
}
