package com.uisrael.gestionactivosapi.presentacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuloDTO {

    private Integer idModulo;
    private String codigo;
    private String nombre;
    private String icono;
    private String ruta;
    private Integer orden;
    private boolean estado;
    private boolean asignado;
}
