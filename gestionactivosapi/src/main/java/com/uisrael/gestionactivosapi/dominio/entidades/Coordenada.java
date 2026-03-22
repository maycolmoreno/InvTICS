package com.uisrael.gestionactivosapi.dominio.entidades;

import java.math.BigDecimal;

public record Coordenada(BigDecimal latitud, BigDecimal longitud) {

    public Coordenada {
        if (latitud != null && (latitud.compareTo(BigDecimal.valueOf(-90)) < 0
                || latitud.compareTo(BigDecimal.valueOf(90)) > 0)) {
            throw new IllegalArgumentException("Latitud fuera de rango");
        }
        if (longitud != null && (longitud.compareTo(BigDecimal.valueOf(-180)) < 0
                || longitud.compareTo(BigDecimal.valueOf(180)) > 0)) {
            throw new IllegalArgumentException("Longitud fuera de rango");
        }
    }
}
