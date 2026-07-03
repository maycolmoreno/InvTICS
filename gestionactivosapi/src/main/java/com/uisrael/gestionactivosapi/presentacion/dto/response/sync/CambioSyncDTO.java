package com.uisrael.gestionactivosapi.presentacion.dto.response.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Cambio relevante detectado durante una sincronizacion. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CambioSyncDTO {

    private String cedula;
    private String tipo;
    private String detalle;
    private Integer idCustodio;
}
