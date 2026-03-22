package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ImagenMantenimientoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.MantenimientoManualRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoManualResponseDTO;

public interface IMantenimientoManualServicio {

    MantenimientoManualResponseDTO crear(MantenimientoManualRequestDTO request);

    void guardarImagenes(Integer idMantenimiento, List<ImagenMantenimientoRequestDTO> imagenes);

    List<MantenimientoManualResponseDTO> listarTodos();

    MantenimientoManualResponseDTO obtenerDetalle(Integer id);

    List<MantenimientoManualResponseDTO> obtenerHistorial(Integer equipoId);

    MantenimientoManualResponseDTO cerrar(Integer id);
}
