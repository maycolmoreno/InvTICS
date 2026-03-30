package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ActividadChecklistRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.ActividadChecklistResponseDTO;

public interface IActividadChecklistServicio {
    List<ActividadChecklistResponseDTO> listarActivas();
    ActividadChecklistResponseDTO obtenerPorId(Integer id);
    void crear(ActividadChecklistRequestDTO dto);
    void actualizar(Integer id, ActividadChecklistRequestDTO dto);
    void eliminar(Integer id);
}
