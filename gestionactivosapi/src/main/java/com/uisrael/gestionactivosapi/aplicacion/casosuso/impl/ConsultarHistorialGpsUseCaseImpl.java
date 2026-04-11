package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IConsultarHistorialGpsUseCase;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UbicacionTecnicoPort;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionTecnicoJpa;

public class ConsultarHistorialGpsUseCaseImpl implements IConsultarHistorialGpsUseCase {

    private final UbicacionTecnicoPort ubicacionPort;

    public ConsultarHistorialGpsUseCaseImpl(UbicacionTecnicoPort ubicacionPort) {
        this.ubicacionPort = ubicacionPort;
    }

    @Override
    public List<UbicacionTecnicoJpa> ejecutar(LocalDate fecha) {
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.atTime(LocalTime.MAX);
        return ubicacionPort.historialPorFecha(desde, hasta);
    }
}
