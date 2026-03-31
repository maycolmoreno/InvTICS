package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ActividadPlanificadaRequestDTO {
    private Integer tecnicoId;
    private Integer creadoPorId;
    private String titulo;
    private String descripcion;
    private String tipoActividad;
    private String prioridad;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer tiempoEstimadoMinutos;
    private Integer referenciaMantenimientoId;
    private String observaciones;
}
