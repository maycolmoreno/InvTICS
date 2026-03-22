package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;

public interface IMantenimientoRepository {

    Mantenimientos guardar(Mantenimientos mantenimiento);

    List<Mantenimientos> guardarTodos(List<Mantenimientos> mantenimientos);

    Optional<Mantenimientos> buscarPorId(int id);

    Integer obtenerMaxSecuenciaPorYear(int yearSnapshoted);

    LocalDateTime obtenerUltimoCierrePorEquipo(int equipoId);
}
