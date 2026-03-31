package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IConsultarUbicacionesTiempoRealUseCase;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UbicacionTecnicoPort;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionTecnicoJpa;

public class ConsultarUbicacionesTiempoRealUseCaseImpl implements IConsultarUbicacionesTiempoRealUseCase {

    private final UbicacionTecnicoPort ubicacionPort;

    public ConsultarUbicacionesTiempoRealUseCaseImpl(UbicacionTecnicoPort ubicacionPort) {
        this.ubicacionPort = ubicacionPort;
    }

    @Override
    public List<UbicacionTecnicoJpa> ejecutar() {
        // Retorna última posición de cada técnico de las últimas 2 horas
        LocalDateTime desde = LocalDateTime.now().minusHours(2);
        return ubicacionPort.ultimasPorTecnicosActivos(desde);
    }
}
