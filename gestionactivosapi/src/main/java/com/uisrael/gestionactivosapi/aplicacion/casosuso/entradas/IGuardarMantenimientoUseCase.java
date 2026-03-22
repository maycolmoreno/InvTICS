package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.presentacion.dto.request.ActividadRealizadaRequestDTO;

public interface IGuardarMantenimientoUseCase {

    void guardar(Integer idMantenimiento, List<ActividadRealizadaRequestDTO> actividades,
            String observaciones, String estadoGeneral, String firmaBase64);
}
