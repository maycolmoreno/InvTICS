package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
