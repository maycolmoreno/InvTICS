package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class OrdenGuardarRequestDTO {

    private String estadoGeneral;
    private String observaciones;
    private String firmaBase64;
    private List<ActividadRealizadaRequestDTO> actividades;
}
