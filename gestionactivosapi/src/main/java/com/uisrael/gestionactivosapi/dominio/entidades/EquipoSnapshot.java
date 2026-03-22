package com.uisrael.gestionactivosapi.dominio.entidades;

public record EquipoSnapshot(String serieSnapshot, String sineSnapshot, Integer yearSnapshoted) {

    public EquipoSnapshot {
        if (serieSnapshot != null && serieSnapshot.length() > 120) {
            throw new IllegalArgumentException("serieSnapshot no puede exceder 120 caracteres");
        }
        if (sineSnapshot != null && sineSnapshot.length() > 50) {
            throw new IllegalArgumentException("sineSnapshot no puede exceder 50 caracteres");
        }
        if (yearSnapshoted != null && yearSnapshoted < 2000) {
            throw new IllegalArgumentException("yearSnapshoted no puede ser menor a 2000");
        }
    }
}
