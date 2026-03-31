package com.uisrael.gestionactivosapi.presentacion.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConsentimientoRequestDTO {

    @NotNull(message = "El ID del técnico es obligatorio")
    private Integer tecnicoId;

    private LocalDateTime fechaAceptacion;

    private String versionTerminos;

    private String ipAceptacion;
}
