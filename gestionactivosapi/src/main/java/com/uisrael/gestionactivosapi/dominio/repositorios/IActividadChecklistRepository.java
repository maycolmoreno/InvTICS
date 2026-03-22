package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.ActividadChecklist;

public interface IActividadChecklistRepository {

    List<ActividadChecklist> listarActivas();

    List<ActividadChecklist> listarActivasPorCategoria(Integer idCategoria);
}
