package com.uisrael.gestionactivosapi.presentacion.dto.request;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class MantenimientoManualRequestDTO {
    private Integer equipoId;
    private Integer custodioId;
    private String tipoMantenimiento;
    private LocalDate fechaMantenimiento;
    private LocalDate proximaFecha;
    private String detalle;
    private String estadoGeneral;
    private String firmaTecnico;
    private String firmaCustodio;
    private String ipOrigen;
    private List<ActividadManualRequestDTO> actividades;
    private List<ImagenMantenimientoRequestDTO> imagenes;
}
