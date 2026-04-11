package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import java.time.LocalDateTime;
import java.util.List;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionTecnicoJpa;

public interface UbicacionTecnicoPort {

    UbicacionTecnicoJpa guardar(UbicacionTecnicoJpa ubicacion);

    List<UbicacionTecnicoJpa> ultimasPorTecnicosActivos(LocalDateTime desde);

    List<UbicacionTecnicoJpa> historialPorFecha(LocalDateTime desde, LocalDateTime hasta);

    int eliminarAnterioresA(LocalDateTime fecha);
}
