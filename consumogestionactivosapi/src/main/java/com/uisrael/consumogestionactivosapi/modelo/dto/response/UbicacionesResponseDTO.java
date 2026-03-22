package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import lombok.Data;

@Data
public class UbicacionesResponseDTO {

	private int idUbicacion;
	private String nombre;
	private String agencia;
	private boolean estado;
	private String latitud;
	private String longitud;
	private String direccion;
	private String ciudad;
	private String parroquia;
	private String provincia;
	private String linkCoordenada;

}
