package com.uisrael.gestionactivosapi.aplicacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Comando para registrar una firma en un mantenimiento.
 */
public record RegistrarFirmaCommand(
        @NotNull(message = "El id del mantenimiento es obligatorio")
        Integer mantenimientoId,

        @NotNull(message = "El id del usuario firmante es obligatorio")
        Integer firmadoPorId,

        @NotBlank(message = "El tipo de firma es obligatorio")
        String tipoFirma,

        @NotBlank(message = "La firma en base64 es obligatoria")
        String firmaBase64,

        String ipOrigen
) {
}
