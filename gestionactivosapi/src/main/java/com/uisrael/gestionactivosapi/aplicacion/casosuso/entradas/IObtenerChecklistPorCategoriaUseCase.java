package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.ActividadChecklist;

public interface IObtenerChecklistPorCategoriaUseCase {

    List<ActividadChecklist> ejecutar(Integer idCategoria);
}
