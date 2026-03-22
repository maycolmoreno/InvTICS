package com.uisrael.gestionactivosapi.presentacion.dto.request;

import lombok.Data;

@Data
public class ImagenMantenimientoRequestDTO {
    private String nombreArchivo;
    private String rutaArchivo;
    private Long tamanioBytes;
}
