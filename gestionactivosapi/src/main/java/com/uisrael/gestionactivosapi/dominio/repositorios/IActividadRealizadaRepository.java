package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.ActividadRealizada;

public interface IActividadRealizadaRepository {

    void eliminarPorMantenimiento(int idMantenimiento);

    List<ActividadRealizada> guardarTodas(List<ActividadRealizada> actividades);

    List<ActividadRealizada> listarPorMantenimiento(int idMantenimiento);
}
