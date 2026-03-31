package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MetricasCumplimientoResponseDTO {
    private Integer tecnicoId;
    private String tecnicoNombre;
    private String periodo;
    private long totalActividades;
    private long completadas;
    private long pendientes;
    private long enProgreso;
    private long vencidas;
    private long canceladas;
    private double porcentajeCompletadas;
    private double porcentajeCumplimientoATiempo;
    private long completadasATiempo;
    private long completadasTarde;
    private double tiempoPromedioMinutos;
}
