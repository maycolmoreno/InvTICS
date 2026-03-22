package com.uisrael.gestionactivosapi.presentacion.dto.request;

import lombok.Data;

@Data
public class MantenimientoProgramadoRequestDTO {
    private Integer equipoId;
    private Integer tecnicoId;
    private Integer frecuenciaDias;
    private String observaciones;
}
