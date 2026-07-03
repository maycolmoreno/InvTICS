package com.uisrael.consumogestionactivosapi.service;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.sync.EstadoSincronizacionDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.sync.SincronizacionResultadoDTO;

public interface ISyncEmpleadosServicio {

    EstadoSincronizacionDTO obtenerEstado();

    SincronizacionResultadoDTO sincronizarManual(String json);

    SincronizacionResultadoDTO sincronizarDesdeFuente();
}
