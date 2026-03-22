package com.uisrael.gestionactivosapi.presentacion.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RolesRequestDTO {

	private int idRol;

	@Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
	@Pattern(regexp = "^[\\p{L}\\s]+$", message = "El nombre solo puede contener letras y espacios")
	private String nombre;

	private boolean estado;

}

