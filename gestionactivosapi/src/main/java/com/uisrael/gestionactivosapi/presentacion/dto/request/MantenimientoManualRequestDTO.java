package com.uisrael.gestionactivosapi.presentacion.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MantenimientoManualRequestDTO {
    @NotNull(message = "equipoId es obligatorio")
    private Integer equipoId;
    @NotNull(message = "custodioId es obligatorio")
    private Integer custodioId;
    @NotBlank(message = "tipoMantenimiento es obligatorio")
    private String tipoMantenimiento;
    @NotNull(message = "fechaMantenimiento es obligatoria")
    private LocalDate fechaMantenimiento;
    private LocalDate proximaFecha;
    @NotBlank(message = "detalle es obligatorio")
    private String detalle;
    @NotBlank(message = "estadoGeneral es obligatorio")
    private String estadoGeneral;
    private String firmaTecnico;
    private String firmaCustodio;
    private String ipOrigen;
    private List<ActividadManualRequestDTO> actividades;
    private List<ImagenMantenimientoRequestDTO> imagenes;
}
