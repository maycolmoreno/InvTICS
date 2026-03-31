package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para ActualizacionActivo.
 * El nombre del usuario se obtiene via JOIN a la tabla usuarios
 * a través de fk_usuario_actualizacion (la columna varchar fue eliminada).
 */
public record ActualizacionActivoResponseDTO(
    Integer id,
    Integer activoId,
    LocalDateTime fechaActualizacion,
    String descripcion,
    Integer usuarioId,
    String usuarioNombre
) {}
