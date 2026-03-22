package com.uisrael.gestionactivosapi.presentacion.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImagenMantenimientoResponseDTO {
    private Long idImagen;
    private String nombreArchivo;
    private String rutaArchivo;
    private Long tamanioBytes;
}
