package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActividadPlanificadaResponseDTO {
    private Long idActividadPlanificada;
    private Integer tecnicoId;
    private String tecnicoNombre;
    private Integer creadoPorId;
    private String creadoPorNombre;
    private String titulo;
    private String descripcion;
    private String tipoActividad;
    private String prioridad;
    private String estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDateTime fechaCompletada;
    private Integer tiempoEstimadoMinutos;
    private Integer tiempoRealMinutos;
    private Integer referenciaMantenimientoId;
    private Integer fkEquipoId;
    private String equipoNombre;
    private Integer fkUbicacionId;
    private String ubicacionNombre;
    private String observaciones;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
