package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class EquiposResponseDTO {

    private int idEquipo;

    private String codigoSap;
    private String modelo;
    private String serial;
    private String procesador;

    private Integer memoriaRamGb;
    private Integer capacidadAlmacenamientoGb;

    private Boolean licenciaWindowsActivada;

    private String mac;

    private String serie;
    private Long idCliente;
    private Long empresaId;

    private LocalDate fechaCompra;
    private BigDecimal precioCompra;

    private String estadoEquipo;
    private String observacionEquipo;

    private boolean estado;


    private MarcasResponseDTO fkMarca;
    private CategoriaEquiposResponseDTO fkCategoria;
    private UbicacionesResponseDTO fkUbicacion;
}
