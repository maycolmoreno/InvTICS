package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Activo;

public interface ActivoRepositorioPuerto {

    Activo guardar(Activo activo);

    Optional<Activo> obtenerPorId(Integer id);

    List<Activo> obtenerTodos();

    void actualizar(Activo activo);

    void eliminar(Integer id);

    default Optional<Activo> buscarPorId(int id) {
        return obtenerPorId(id);
    }

    default List<Activo> listarTodos() {
        return obtenerTodos();
    }
}
