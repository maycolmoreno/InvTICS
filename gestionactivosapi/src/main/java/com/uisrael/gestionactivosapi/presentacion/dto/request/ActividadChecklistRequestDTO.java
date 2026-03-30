package com.uisrael.gestionactivosapi.presentacion.dto.request;

public record ActividadChecklistRequestDTO(
        String nombre,
        String categoria,
        Integer orden,
        Boolean estado) {
}
