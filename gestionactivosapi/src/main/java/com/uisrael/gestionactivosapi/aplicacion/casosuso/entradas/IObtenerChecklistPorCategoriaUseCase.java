package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.ActividadChecklist;

public interface IObtenerChecklistPorCategoriaUseCase {

    List<ActividadChecklist> listarActivas();

    ActividadChecklist obtenerPorId(Integer id);

    List<ActividadChecklist> ejecutar(Integer idCategoria);

    ActividadChecklist crear(ActividadChecklist actividad);

    ActividadChecklist actualizar(Integer id, ActividadChecklist actividad);

    void eliminar(Integer id);
}
