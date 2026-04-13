package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import lombok.Data;

@Data
public class EquipoEnMantenimientoDTO {
    private Integer equipoId;
    private String codigoSap;
    private String descripcion;
    private String serial;
}
