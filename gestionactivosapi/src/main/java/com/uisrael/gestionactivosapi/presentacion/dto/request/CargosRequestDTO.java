package com.uisrael.gestionactivosapi.presentacion.dto.request;

import lombok.Data;

@Data
public class CargosRequestDTO {

	private int idCargo;
	private String nombre;
	private boolean estado;

	private DepartamentosRequestDTO fkDepartamento;
}

