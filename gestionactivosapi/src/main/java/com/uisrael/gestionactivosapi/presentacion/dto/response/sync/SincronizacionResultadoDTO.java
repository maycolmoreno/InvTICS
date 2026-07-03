package com.uisrael.gestionactivosapi.presentacion.dto.response.sync;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Resultado de una corrida de sincronizacion de empleados. */
@Getter
@Setter
@NoArgsConstructor
public class SincronizacionResultadoDTO {

    private Integer idEjecucion;
    private LocalDateTime ejecutadoEn;
    private String origen;
    private String ejecutadoPor;
    private int totalRecibidos;
    private int creados;
    private int actualizados;
    private int inactivados;
    private int reactivados;
    private int sinCambios;
    private int advertencias;
    private List<CambioSyncDTO> cambios;
}
