package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RolesRequestDTO {
	private int idRol;
	private String nombre;
	private boolean estado;
}
