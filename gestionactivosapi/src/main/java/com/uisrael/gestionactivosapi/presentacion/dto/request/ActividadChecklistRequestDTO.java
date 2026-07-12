package com.uisrael.gestionactivosapi.presentacion.dto.request;

public record ActividadChecklistRequestDTO(
        String nombre,
        Integer orden,
        Boolean estado) {
}
