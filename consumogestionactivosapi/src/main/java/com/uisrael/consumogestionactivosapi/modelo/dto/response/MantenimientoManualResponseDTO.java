package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class MantenimientoManualResponseDTO {
    private Integer idMantenimiento;
    private Integer equipoId;
    private String equipoCodigoSap;
    private String equipoDescripcion;
    private Integer custodioId;
    private String custodioNombre;
    private String custodioCorreo;
    private Integer tecnicoId;
    private String tecnicoNombre;
    private String tipoMantenimiento;
    private LocalDate fechaMantenimiento;
    private String detalle;
    private String estadoGeneral;
    private LocalDate proximaFecha;
    private String firmaTecnico;
    private String firmaCustodio;
    private Boolean estado;
    private String estadoInterno;
    private LocalDateTime creadoEn;
    private List<ActividadManualResponseDTO> actividades;
    private List<ImagenMantenimientoResponseDTO> imagenes;
}
