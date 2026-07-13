package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UbicacionesResponseDTO {

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

	private DepartamentosResponseDTO fkDepartamento;
	private Integer idCustodioEncargado;
	private String nombreCustodioEncargado;

}
