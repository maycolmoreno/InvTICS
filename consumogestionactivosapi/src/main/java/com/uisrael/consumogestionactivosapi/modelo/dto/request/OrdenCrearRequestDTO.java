package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class OrdenCrearRequestDTO {

    private List<Integer> equiposIds;
    private String tipo;
    private String prioridad;
    private Integer idUsuarioTecnico;
}
