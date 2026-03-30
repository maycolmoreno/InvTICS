package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos.ActividadRealizadaComando;

public interface IGuardarMantenimientoUseCase {

    void guardar(Integer idMantenimiento, List<ActividadRealizadaComando> actividades,
            String observaciones, String estadoGeneral, String firmaBase64);
}
