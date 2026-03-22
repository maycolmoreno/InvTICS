package com.uisrael.gestionactivosapi.presentacion.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustodiasRequestDTO {

    private int idCustodiaEquipo;

    @NotNull
    private LocalDate fechaInicio;

    // si puede ser null, no le pongas NotNull
    private LocalDate fechaFin;

    @NotNull
    private String observacion;

    private boolean estado;

    // AGREGA ESTO (para relacion)
    @NotNull
    private List<EquiposRequestDTO> equipos;

    @NotNull
    private CustodiosRequestDTO fkCustodio;

    private String tipoMovimiento;
}

