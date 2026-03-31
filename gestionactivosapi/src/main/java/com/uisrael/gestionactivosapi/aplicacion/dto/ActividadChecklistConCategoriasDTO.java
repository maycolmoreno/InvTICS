package com.uisrael.gestionactivosapi.aplicacion.dto;

import java.util.List;

/**
 * DTO que representa una actividad de checklist con sus categorías asociadas
 * obtenidas desde la tabla relacional checklist_categoria.
 */
public record ActividadChecklistConCategoriasDTO(
    Integer idActividad,
    String nombre,
    Integer orden,
    Boolean estado,
    List<CategoriaDTO> categorias
) {
    public record CategoriaDTO(Integer idCategoria, String nombre) {}
}
