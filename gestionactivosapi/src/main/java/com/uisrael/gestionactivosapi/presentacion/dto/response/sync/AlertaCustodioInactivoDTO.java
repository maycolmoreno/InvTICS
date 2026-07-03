package com.uisrael.gestionactivosapi.presentacion.dto.response.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Custodio inactivo que aun mantiene una custodia activa sobre un equipo. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertaCustodioInactivoDTO {

    private Integer idCustodio;
    private String custodioNombre;
    private String cedula;
    private Integer idEquipo;
    private String equipoCodigo;
    private String equipoModelo;
}
