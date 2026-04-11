package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialGpsDTO {

    private Long idUbicacionTecnico;
    private Integer usuarioId;
    private String nombre;
    private String departamento;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal precisionMetros;
    private LocalDateTime timestampCaptura;
}
