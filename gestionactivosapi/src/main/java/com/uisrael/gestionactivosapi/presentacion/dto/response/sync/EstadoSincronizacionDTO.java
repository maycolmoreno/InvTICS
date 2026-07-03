package com.uisrael.gestionactivosapi.presentacion.dto.response.sync;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Estado actual de la sincronizacion de empleados: ultima corrida, sus
 * cambios y las alertas vigentes (custodios inactivos con custodias activas).
 */
@Getter
@Setter
@NoArgsConstructor
public class EstadoSincronizacionDTO {

    private boolean fuenteConfigurada;
    private String fuenteDescripcion;
    private SincronizacionResultadoDTO ultimaEjecucion;
    private List<AlertaCustodioInactivoDTO> alertas;
}
