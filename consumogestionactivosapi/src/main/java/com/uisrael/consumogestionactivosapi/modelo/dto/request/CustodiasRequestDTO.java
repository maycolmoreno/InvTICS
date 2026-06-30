package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class CustodiasRequestDTO {

    // =========================
    // CABECERA (ACTA)
    // =========================
    private int idCustodia;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    private String observacion;
    private String motivoBaja;
    private boolean estado;

    private CustodiosRequestDTO fkCustodio;

    // =========================
    // CREAR: selección por checkbox
    // =========================
    private List<Integer> equiposSeleccionados;
    private List<EquiposRequestDTO> equipos;
    private int idCustodio; // ✅ necesario para cerrar por custodio

    // =========================
    // DETALLE (edición 1 línea)
    // =========================
    private int idCustodiaEquipo;
    private EquiposRequestDTO fkEquipo;

    // =========================
    // CIERRE: checklist devolución
    // =========================
    private List<Integer> detallesEntregados;
    private Integer bodegaDestinoId;
    private String estadoFisicoRetorno;

    private String tipoMovimiento;
}
