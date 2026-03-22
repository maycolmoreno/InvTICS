package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import lombok.Data;

@Data
public class ImagenMantenimientoRequestDTO {
    private String nombreArchivo;
    private String rutaArchivo;
    private Long tamanioBytes;
}
