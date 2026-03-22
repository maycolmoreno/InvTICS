package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import com.uisrael.gestionactivosapi.dominio.dto.HistorialCompletoDTO;

public interface IObtenerHistorialEquipoUseCase {

    HistorialCompletoDTO obtener(Long equipoId);
}
