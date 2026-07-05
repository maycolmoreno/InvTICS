package com.uisrael.gestionactivosapi.presentacion.dto.response.sync;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resultado de crear/actualizar un custodio local a partir de una persona
 * ubicada en el directorio institucional externo (resolucion bajo demanda,
 * usada al asignar un activo a alguien que aun no tiene registro local).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustodioResueltoDTO {

    private Integer idCustodio;
    private String nombre;
    private String cedula;
    private String cargo;
    private String departamento;
    private boolean creado;
    private List<String> advertencias;
}
