package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.FilaImportDTO;

public interface IImportarServicio {

    /**
     * Parsea el archivo Excel y retorna una lista de filas listas para previsualizar.
     * Resuelve (o crea) las marcas necesarias durante el parseo.
     */
    List<FilaImportDTO> parsearExcel(MultipartFile archivo, int categoriaIdDefecto) throws Exception;

    /**
     * Importa los equipos hacia la API.  Cada fila se expande según su campo "cant".
     * Retorna el número de equipos creados exitosamente.
     */
    int importarEquipos(List<FilaImportDTO> filas);
}
