package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class ActividadChecklistResponseDTO {
    private Integer idActividad;
    private String nombre;
    private List<String> categorias;
    private Integer orden;
    private Boolean estado;

    /**
     * Retorna la primera categoría de la lista (compatibilidad con código existente).
     */
    public String getCategoria() {
        return categorias != null && !categorias.isEmpty() ? categorias.get(0) : null;
    }
}
