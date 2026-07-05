package com.uisrael.consumogestionactivosapi.modelo.dto.response.sync;

import lombok.Data;

/**
 * Candidato encontrado en vivo en el directorio institucional externo,
 * antes de decidir si se registra como custodio local.
 */
@Data
public class CandidatoDirectorioDTO {

    private String cedula;
    private String nombre;
    private String cargo;
    private String departamento;
}
