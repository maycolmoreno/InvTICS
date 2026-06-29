package com.uisrael.gestionactivosapi.presentacion.dto.request;

import lombok.Data;

@Data
public class CerrarMantenimientoRequestDTO {
    private String descripcionTrabajoRealizado;
    private String resultadoTecnico;
    private String observacionCierre;
}
