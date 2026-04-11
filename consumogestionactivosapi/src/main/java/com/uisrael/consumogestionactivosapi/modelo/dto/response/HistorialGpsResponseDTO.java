package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HistorialGpsResponseDTO {

	private Long idUbicacionTecnico;
	private Integer usuarioId;
	private String nombre;
	private String departamento;
	private Double latitud;
	private Double longitud;
	private Double precisionMetros;
	private LocalDateTime timestampCaptura;
}
