package com.uisrael.consumogestionactivosapi.modelo.dto.response.sync;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Cambio relevante detectado durante una sincronizacion (espejo del backend). */
@Getter
@Setter
@NoArgsConstructor
public class CambioSyncDTO {

    private String cedula;
    private String tipo;
    private String detalle;
    private Integer idCustodio;
}
