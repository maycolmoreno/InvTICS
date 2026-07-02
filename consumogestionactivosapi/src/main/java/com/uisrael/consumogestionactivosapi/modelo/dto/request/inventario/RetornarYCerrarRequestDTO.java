package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

import java.time.LocalDate;

import lombok.Data;

/** Fase C3: request compuesto (retornar de reparacion + cerrar OT) enviado al API. */
@Data
public class RetornarYCerrarRequestDTO {
    private Integer equipoId;
    private Integer bodegaDestinoId;
    private String condicion;
    private LocalDate fechaRetorno;
    private String observacion;
    private String resultadoTecnico;
    private String observacionCierre;
}
