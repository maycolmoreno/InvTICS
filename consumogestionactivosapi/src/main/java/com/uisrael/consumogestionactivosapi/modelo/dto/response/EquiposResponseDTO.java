package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ActivoInventarioResponseDTO;

import lombok.Data;

@Data
public class EquiposResponseDTO {

    public static EquiposResponseDTO desdeActivo(ActivoInventarioResponseDTO activo) {
        EquiposResponseDTO dto = new EquiposResponseDTO();
        dto.setIdEquipo(activo.getIdEquipo() != null ? activo.getIdEquipo() : 0);
        dto.setCodigoCresio(activo.getCodigoCresio());
        dto.setCodigoSap(activo.getCodigoSap());
        dto.setModelo(activo.getModelo());
        dto.setSerial(activo.getSerial());
        return dto;
    }

    private int idEquipo;

    private String codigoCresio;
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

    private LocalDate fechaAdquisicion;
    private Double valorActual;
    private String descripcion;


    private MarcasResponseDTO fkMarca;
    private CategoriaEquiposResponseDTO fkCategoria;
}
