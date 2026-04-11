package com.uisrael.gestionactivosapi.presentacion.dto.request;

import lombok.Data;

@Data
public class DepartamentosRequestDTO {

	private int idDepartamento;
	private String nombre;
	private String tipo;
	private boolean estado;

}

