package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;

public interface IMantenimientosRepositorio {

    Mantenimientos guardar(Mantenimientos mantenimiento);

    List<Mantenimientos> listarTodos();

    Optional<Mantenimientos> buscarPorId(int id);
}
