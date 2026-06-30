package com.uisrael.gestionactivosapi.presentacion.dto.request.inventario;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class BajaActivoRequestDTO {

    @NotNull
    private Integer equipoId;

    private LocalDate fechaBaja;

    @NotBlank
    @Pattern(
        regexp = "DESTRUCCION|OBSOLESCENCIA|ROBO_PERDIDA|DONACION|DANIO_IRREPARABLE|OTRO",
        message = "Motivo inválido"
    )
    private String motivo;

    @NotBlank(message = "La observación es obligatoria")
    private String observacion;

    private String autorizadoPor;

    public Integer getEquipoId() { return equipoId; }
    public void setEquipoId(Integer equipoId) { this.equipoId = equipoId; }
    public LocalDate getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(LocalDate fechaBaja) { this.fechaBaja = fechaBaja; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public String getAutorizadoPor() { return autorizadoPor; }
    public void setAutorizadoPor(String autorizadoPor) { this.autorizadoPor = autorizadoPor; }
}
