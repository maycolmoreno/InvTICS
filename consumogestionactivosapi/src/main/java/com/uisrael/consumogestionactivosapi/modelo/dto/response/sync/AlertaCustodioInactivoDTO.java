package com.uisrael.consumogestionactivosapi.modelo.dto.response.sync;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Custodio inactivo con custodia activa (espejo del backend). */
@Getter
@Setter
@NoArgsConstructor
public class AlertaCustodioInactivoDTO {

    private Integer idCustodio;
    private String custodioNombre;
    private String cedula;
    private Integer idEquipo;
    private String equipoCodigo;
    private String equipoModelo;
}
