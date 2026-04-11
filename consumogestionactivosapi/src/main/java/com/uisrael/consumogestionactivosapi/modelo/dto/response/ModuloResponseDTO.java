package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import lombok.Data;

@Data
public class ModuloResponseDTO {

	private Integer idModulo;
	private String codigo;
	private String nombre;
	private String icono;
	private String ruta;
	private Integer orden;
	private boolean estado;
	private boolean asignado;
}
