package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EquiposRequestDTO {

    private int idEquipo;

    @NotBlank(message = "El codigo SAP es requerido")
    private String codigoSap;

    @NotBlank(message = "El modelo es requerido")
    private String modelo;

    private String serial;
    private String procesador;

    @Positive(message = "La memoria RAM debe ser positiva")
    private Integer memoriaRamGb;

    @Positive(message = "La capacidad de almacenamiento debe ser positiva")
    private Integer capacidadAlmacenamientoGb;

    private Boolean licenciaWindowsActivada;
    private String mac;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaCompra;

    private BigDecimal precioCompra;
    private String estadoEquipo;
    private String observacionEquipo;

    private boolean estado;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaAdquisicion;
    private Double valorActual;
    private String descripcion;

    private MarcasRequestDTO fkMarca;
    private CategoriaEquiposRequestDTO fkCategoria;
}
