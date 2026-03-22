package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class MantenimientoEquiposRequestDTO {

	private Integer equipoId;
	private Integer custodioId;
	private String correoCliente;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fechaMantenimiento;
	private String tipoMantenimiento;
	private String asunto;
	private String detalleMantenimiento;
}
