package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class VisitaEquipoResponseDTO {

    private int idEquipo;
    private String serial;
    private String marca;
    private String modelo;
    private String codigoSap;
    private String custodioNombre;
    private String custodioArea;
    private String ubicacionNombre;
    private LocalDate fechaUltimoMantenimiento;
    private Long diasSinMantenimiento;
    private String estadoMantenimiento;
}
