package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import lombok.Data;

@Data
public class UbicacionActivaResponseDTO {

	private Integer usuarioId;
	private String nombre;
	private String departamento;
	private Double latitud;
	private Double longitud;
	private Double precisionMetros;
	private Integer minutosAtras;
}
