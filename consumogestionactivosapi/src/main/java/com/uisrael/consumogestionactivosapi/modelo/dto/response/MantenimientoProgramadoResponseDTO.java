package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MantenimientoProgramadoResponseDTO {
    private Long idProgramado;
    private Integer equipoId;
    private String equipoCodigoSap;
    private String equipoDescripcion;
    private Integer tecnicoId;
    private String tecnicoNombre;
    private Integer frecuenciaDias;
    private LocalDate fechaUltimoMantenimiento;
    private LocalDate fechaProximoMantenimiento;
    private Boolean estado;
    private String observaciones;
}
