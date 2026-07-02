package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

import java.time.LocalDate;

import lombok.Data;

/** Fase C3: request compuesto (enviar a reparacion + crear OT) enviado al API. */
@Data
public class EnviarConOtRequestDTO {
    private Integer equipoId;
    private String motivo;
    private String proveedorTecnico;
    private LocalDate fechaEnvio;
    private String observacion;
    private Integer custodioId;
    private String firmaTecnico;
    private String detalle;
    private LocalDate proximaFecha;
}
