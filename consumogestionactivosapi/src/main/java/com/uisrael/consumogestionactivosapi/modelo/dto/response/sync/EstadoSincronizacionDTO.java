package com.uisrael.consumogestionactivosapi.modelo.dto.response.sync;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Estado de la sincronizacion de empleados (espejo del backend). */
@Getter
@Setter
@NoArgsConstructor
public class EstadoSincronizacionDTO {

    private boolean fuenteConfigurada;
    private String fuenteDescripcion;
    private SincronizacionResultadoDTO ultimaEjecucion;
    private List<AlertaCustodioInactivoDTO> alertas;
}
