package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionTecnicoJpa;

import java.util.List;

public interface IConsultarUbicacionesTiempoRealUseCase {

    List<UbicacionTecnicoJpa> ejecutar();
}
