package com.uisrael.consumogestionactivosapi.modelo.dto.response.sync;

import java.util.List;

import lombok.Data;

/**
 * Resultado de crear/actualizar un custodio local a partir de una persona
 * ubicada en el directorio institucional externo.
 */
@Data
public class CustodioResueltoDTO {

    private Integer idCustodio;
    private String nombre;
    private String cedula;
    private String cargo;
    private String departamento;
    private boolean creado;
    private List<String> advertencias;
}
