package com.uisrael.gestionactivosapi.presentacion.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UbicacionesRequestDTO {

	private int idUbicacion;
	private String nombre;
	private String agencia;

	private boolean estado;

	private BigDecimal latitud;
	private BigDecimal longitud;
	private String direccion;
	private String ciudad;
	private String parroquia;
	private String provincia;
	private String linkCoordenada;

}
