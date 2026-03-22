package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.ActividadChecklistResponseDTO;

public interface IActividadChecklistServicio {
    List<ActividadChecklistResponseDTO> listarActivas();
}
