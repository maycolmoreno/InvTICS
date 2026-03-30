package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.ActividadChecklist;
import java.util.List;
import java.util.Optional;

public interface ActividadChecklistRepositorioPuerto {

    ActividadChecklist guardar(ActividadChecklist actividad);

    Optional<ActividadChecklist> obtenerPorId(Integer id);

    List<ActividadChecklist> listarActivas();

    List<ActividadChecklist> listarActivasPorCategoria(Integer idCategoria);

    void eliminar(Integer id);
}
