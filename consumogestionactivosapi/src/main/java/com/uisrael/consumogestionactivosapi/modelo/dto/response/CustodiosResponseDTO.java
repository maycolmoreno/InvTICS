package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CustodiosResponseDTO {

    private int idCustodio;

    private String nombre;

    private String cedula;

    private String correo;

    private String telefono;

    private LocalDate fechaIngreso;

	private boolean estado;

	private DepartamentosResponseDTO fkDepartamento;

	private CargosResponseDTO fkCargo;

	private UbicacionesResponseDTO fkUbicacion;
}
