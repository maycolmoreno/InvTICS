package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CustodiasResponseDTO {

    // CABECERA
    private int idCustodia; // si lo tienes, déjalo
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String observacion;
    private boolean estado;

    private CustodiosResponseDTO fkCustodio;

    // DETALLE
    private int idCustodiaEquipo;
    private EquiposResponseDTO fkEquipo;

    // ✅ NUEVO: id del empleado (viene como campo directo en JSON)
    private int idCustodio;

    private Boolean entregado;
    private String observacionDevolucion;
    private String tipoMovimiento;
    private String rutaActaPdf;
}
