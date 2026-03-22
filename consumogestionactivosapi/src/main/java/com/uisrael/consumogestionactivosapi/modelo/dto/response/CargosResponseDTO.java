package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import lombok.Data;

@Data
public class CargosResponseDTO {

	private int idCargo;
	private String nombre;
	private boolean estado;
	private DepartamentosResponseDTO fkDepartamento;

}
