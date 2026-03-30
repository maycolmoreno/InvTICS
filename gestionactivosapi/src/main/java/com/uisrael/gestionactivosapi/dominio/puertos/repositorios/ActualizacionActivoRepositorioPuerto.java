package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.ActualizacionActivo;

public interface ActualizacionActivoRepositorioPuerto {

    ActualizacionActivo guardar(ActualizacionActivo actualizacion);

    Optional<ActualizacionActivo> obtenerPorId(Integer id);

    List<ActualizacionActivo> obtenerTodos();

    void actualizar(ActualizacionActivo actualizacion);

    void eliminar(Integer id);

    default Optional<ActualizacionActivo> buscarPorId(int id) {
        return obtenerPorId(id);
    }

    default List<ActualizacionActivo> listarTodos() {
        return obtenerTodos();
    }
}
