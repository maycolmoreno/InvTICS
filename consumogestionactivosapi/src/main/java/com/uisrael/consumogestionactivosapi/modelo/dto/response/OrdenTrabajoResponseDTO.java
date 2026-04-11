package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrdenTrabajoResponseDTO {

    private Integer idMantenimiento;
    private String sineSnapshoted;
    private LocalDateTime creadoEn;
    private String estadoInterno;
    private String tipoMantenimiento;
    private String prioridad;

    private Integer idEquipo;
    private String serial;
    private String marca;
    private String modelo;
    private String codigoSap;

    private String custodioNombre;
    private String ubicacionNombre;

    private Long diasSinMantenimiento;
    private LocalDate fechaUltimoMantenimiento;

    private List<OrdenActividadResponseDTO> actividades;
}
