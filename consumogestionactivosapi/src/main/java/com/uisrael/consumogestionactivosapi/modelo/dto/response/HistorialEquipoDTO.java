package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class HistorialEquipoDTO {

    private Integer idEquipo;
    private String marca;
    private String modelo;
    private String serial;
    private String codigoSap;
    private LocalDate fechaCompra;
    private String estadoEquipo;
    private String procesador;
    private Integer memoriaRamGb;
    private Integer capacidadAlmacenamientoGb;
    private Boolean licenciaWindowsActivada;
    private String categoriaNombre;

    private String custodioNombre;
    private String departamentoNombre;
    private String ubicacionNombre;
    private String ubicacionCiudad;
    private LocalDate fechaInicioCustodio;
}
