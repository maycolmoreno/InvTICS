package com.uisrael.gestionactivosapi.presentacion.dto.response.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Candidato encontrado en vivo en el directorio institucional externo,
 * antes de decidir si se registra como custodio local.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidatoDirectorioDTO {

    private String cedula;
    private String nombre;
    private String cargo;
    private String departamento;
    private String correo;
    private String telefono;
}
