package com.uisrael.consumogestionactivosapi.service;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.HistorialCompletoDTO;

public interface IHistorialEquipoServicio {

    HistorialCompletoDTO obtenerHistorial(Long equipoId);
}
