package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.ActividadRealizada;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad ActividadRealizada.
 * Define el contrato para operaciones de persistencia de actividades completadas en mantenimiento.
 */
public interface ActividadRealizadaRepositorioPuerto {

    default ActividadRealizada guardar(ActividadRealizada actividad) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    default Optional<ActividadRealizada> obtenerPorId(Integer id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    default List<ActividadRealizada> obtenerTodas() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    default void actualizar(ActividadRealizada actividad) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    default void eliminar(Integer id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    default List<ActividadRealizada> obtenerPorMantenimiento(Integer mantenimientoId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    default List<ActividadRealizada> obtenerPorTecnico(Integer tecnicoId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void eliminarPorMantenimiento(int mantenimientoId);

    List<ActividadRealizada> guardarTodas(List<ActividadRealizada> actividades);

    default List<ActividadRealizada> listarPorMantenimiento(int idMantenimiento) {
        return obtenerPorMantenimiento(idMantenimiento);
    }
}
