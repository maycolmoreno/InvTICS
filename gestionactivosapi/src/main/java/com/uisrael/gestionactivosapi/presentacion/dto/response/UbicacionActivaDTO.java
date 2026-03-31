package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionActivaDTO {

    private Integer usuarioId;
    private String nombre;
    private String departamento;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal precisionMetros;
    private Long minutosAtras;
}
