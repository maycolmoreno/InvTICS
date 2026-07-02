package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.MantenimientoProgramadoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoProgramadoResponseDTO;

public interface IMantenimientoProgramadoServicio {

    List<MantenimientoProgramadoResponseDTO> listarTodos();

    List<MantenimientoProgramadoResponseDTO> listarVencidosYProximos();

    MantenimientoProgramadoResponseDTO obtenerPorEquipo(Integer equipoId);

    MantenimientoProgramadoResponseDTO guardar(MantenimientoProgramadoRequestDTO request);

    void desactivar(Long idProgramado);
}
