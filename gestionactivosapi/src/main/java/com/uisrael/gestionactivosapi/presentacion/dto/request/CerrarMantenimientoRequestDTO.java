package com.uisrael.gestionactivosapi.presentacion.dto.request;

import com.uisrael.gestionactivosapi.dominio.entidades.ResultadoTecnico;

import lombok.Data;

@Data
public class CerrarMantenimientoRequestDTO {
    private String descripcionTrabajoRealizado;
    private ResultadoTecnico resultadoTecnico;
    private String observacionCierre;
}
