package com.uisrael.gestionactivosapi.presentacion.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricasCumplimientoResponseDTO {
    private Integer tecnicoId;
    private String tecnicoNombre;
    private String periodo;

    // Totales
    private long totalActividades;
    private long completadas;
    private long pendientes;
    private long enProgreso;
    private long vencidas;
    private long canceladas;

    // Porcentajes
    private double porcentajeCompletadas;
    private double porcentajeCumplimientoATiempo;

    // Tiempos
    private long completadasATiempo;
    private long completadasTarde;
    private double tiempoPromedioMinutos;
}
