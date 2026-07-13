package com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class CentroOperacionalDTO {
    private List<WorkQueueResumenDTO> bandejas = new ArrayList<>();
    private List<RiesgoOperativoDTO> riesgos = new ArrayList<>();
    private List<QuickActionDTO> quickActions = new ArrayList<>();
    private List<MovimientoRecienteDTO> movimientosRecientes = new ArrayList<>();

    private long totalActivos;
    private long activosAsignados;
    private long activosEnBodega;
    private long activosEnReparacion;
    private long activosEnTransito;

    private Map<String, Long> activosPorCategoria = new LinkedHashMap<>();
    private List<CustodioTopDTO> top5Custodios = new ArrayList<>();
    private Map<String, Long> activosPorUbicacion = new LinkedHashMap<>();

    /** true si alguna consulta al backend fallo: los indicadores pueden estar incompletos. */
    private boolean datosIncompletos;

    public long getPendientesHoy() {
        return bandejas.stream().mapToLong(WorkQueueResumenDTO::getCantidad).sum();
    }
}
