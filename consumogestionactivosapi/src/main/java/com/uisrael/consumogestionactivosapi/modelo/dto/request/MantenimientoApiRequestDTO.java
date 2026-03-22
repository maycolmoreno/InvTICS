package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MantenimientoApiRequestDTO {

	private Integer id;
	private Long equipoId;
	private String serieSnapshot;
	private Long idCliente;
	private Long empresaId;
	private LocalDateTime fechaProgramada;
	private Integer frecuenciaDias;
	private String descripcion;
	private String tipoMantenimiento;
	private String estado;
	private LocalDateTime creadoEn;
	private String estadoInterno;
}
