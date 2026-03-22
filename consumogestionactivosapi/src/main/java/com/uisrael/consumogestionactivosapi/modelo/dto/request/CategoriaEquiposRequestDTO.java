package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import lombok.Data;

@Data
public class CategoriaEquiposRequestDTO {
	private int idCategoria;
	private String nombre;
	private boolean estado;
}
