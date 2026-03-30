package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;

public interface IMantenimientosUseCase {

    Mantenimientos crear(Mantenimientos mantenimiento);

    List<Mantenimientos> listar();

    List<Mantenimientos> listarPorEquipo(int idEquipo);

    Mantenimientos obtenerPorId(int id);
}
