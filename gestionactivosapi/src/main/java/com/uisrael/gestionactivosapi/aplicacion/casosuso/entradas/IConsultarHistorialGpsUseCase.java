package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.time.LocalDate;
import java.util.List;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionTecnicoJpa;

public interface IConsultarHistorialGpsUseCase {

    List<UbicacionTecnicoJpa> ejecutar(LocalDate fecha);
}
