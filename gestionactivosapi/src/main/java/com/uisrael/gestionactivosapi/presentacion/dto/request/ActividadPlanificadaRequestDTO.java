package com.uisrael.gestionactivosapi.presentacion.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActividadPlanificadaRequestDTO {

    @NotNull(message = "El técnico es obligatorio")
    private Integer tecnicoId;

    @NotNull(message = "El creador es obligatorio")
    private Integer creadoPorId;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede superar 200 caracteres")
    private String titulo;

    private String descripcion;

    @NotBlank(message = "El tipo de actividad es obligatorio")
    private String tipoActividad;

    private String prioridad = "MEDIA";

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    private Integer tiempoEstimadoMinutos;

    private Integer referenciaMantenimientoId;

    private String observaciones;
}
