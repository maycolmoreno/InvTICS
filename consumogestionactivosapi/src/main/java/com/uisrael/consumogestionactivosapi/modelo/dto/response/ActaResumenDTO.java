package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDate;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;

import lombok.Data;

@Data
public class ActaResumenDTO {
	private String key;
	private int numeroActa;
	private int idCustodio;
	private int minPk;
	private CustodiosResponseDTO custodio;
	private String tipoMovimiento;
	private String etiquetaTipo;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
	private boolean activa;
	private int cantidadEquipos;
}
