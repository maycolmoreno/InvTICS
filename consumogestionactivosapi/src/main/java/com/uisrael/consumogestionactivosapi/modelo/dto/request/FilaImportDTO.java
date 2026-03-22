package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class FilaImportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sucursal;
    private int cant;
    private String descripcion;
    private String codIdentificacion;
    private String marca;
    private int marcaId;
    private String modelo;
    private String serie;
    private String estadoEquipo;
    private String estacion;
    private boolean etiqueta;
    private String observacion;
    private int categoriaId;
}
