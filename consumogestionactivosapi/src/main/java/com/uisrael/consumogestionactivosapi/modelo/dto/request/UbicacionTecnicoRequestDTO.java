package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import lombok.Data;

@Data
public class UbicacionTecnicoRequestDTO {

	private Integer tecnicoId;
	private Double latitud;
	private Double longitud;
	private Double precisionMetros;
	private String timestampCaptura;
}
