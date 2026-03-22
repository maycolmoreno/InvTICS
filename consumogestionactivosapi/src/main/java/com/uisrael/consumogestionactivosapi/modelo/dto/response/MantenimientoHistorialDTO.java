package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MantenimientoHistorialDTO {

    private Integer idMantenimiento;
    private String sineSnapshoted;
    private String estadoInterno;
    private String descripcion;
    private LocalDateTime fechaCierre;
    private String tecnicoNombre;
    private String tipoInferido;
}
