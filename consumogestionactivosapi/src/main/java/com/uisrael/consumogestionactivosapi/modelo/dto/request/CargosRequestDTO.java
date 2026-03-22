package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CargosRequestDTO {

	private int idCargo;
	private String nombre;
	private boolean estado;
	private DepartamentosRequestDTO fkDepartamento;
}
