package com.uisrael.gestionactivosapi.presentacion.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EquipoEnMantenimientoDTO {
    private Integer equipoId;
    private String codigoSap;
    private String descripcion;
    private String serial;
}
