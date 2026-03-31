package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionTecnicoJpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface IRegistrarUbicacionTecnicoUseCase {

    UbicacionTecnicoJpa ejecutar(Integer tecnicoId, BigDecimal latitud, BigDecimal longitud,
                                  BigDecimal precisionMetros, LocalDateTime timestampCaptura);
}
