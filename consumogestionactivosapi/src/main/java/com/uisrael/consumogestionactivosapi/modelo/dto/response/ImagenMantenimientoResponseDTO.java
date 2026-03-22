package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import lombok.Data;

@Data
public class ImagenMantenimientoResponseDTO {
    private Long idImagen;
    private String nombreArchivo;
    private String rutaArchivo;
    private Long tamanioBytes;
}
