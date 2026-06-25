package com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CentroOperacionalDTO {
    private List<WorkQueueResumenDTO> bandejas = new ArrayList<>();
    private List<RiesgoOperativoDTO> riesgos = new ArrayList<>();
    private List<QuickActionDTO> quickActions = new ArrayList<>();
    private List<MovimientoRecienteDTO> movimientosRecientes = new ArrayList<>();

    public long getPendientesHoy() {
        return bandejas.stream().mapToLong(WorkQueueResumenDTO::getCantidad).sum();
    }
}
