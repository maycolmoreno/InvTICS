package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.ResultadoTecnico;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
    private String descripcionTrabajoRealizado;
    private String estadoGeneral;
    private LocalDate proximaFecha;
    private String firmaTecnico;
    private String firmaCustodio;
    private Boolean estado;
    private String estadoInterno;
    private LocalDateTime creadoEn;
    private ResultadoTecnico resultadoTecnico;
    private String cerradoPor;
    private LocalDateTime cerradoEn;
    private List<ActividadManualResponseDTO> actividades;
    private List<ImagenMantenimientoResponseDTO> imagenes;
    private List<EquipoEnMantenimientoDTO> equipos;
    private Integer totalEquipos;
}
