package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CustodiosResponseDTO {

    private int idCustodio;

    private String nombre;

    private String cedula;

    private String correo;

    private String telefono;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaIngreso;

	private boolean estado;

	private DepartamentosResponseDTO fkDepartamento;

	private CargosResponseDTO fkCargo;

	private UbicacionesResponseDTO fkUbicacion;
}
