package com.uisrael.consumogestionactivosapi.service.impl;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.sync.EstadoSincronizacionDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.sync.SincronizacionResultadoDTO;
import com.uisrael.consumogestionactivosapi.service.ISyncEmpleadosServicio;

@Service
public class SyncEmpleadosServicioImpl implements ISyncEmpleadosServicio {

    private final RestClient clienteWeb;

    public SyncEmpleadosServicioImpl(RestClient clienteWeb) {
        this.clienteWeb = clienteWeb;
    }

    @Override
    public EstadoSincronizacionDTO obtenerEstado() {
        return clienteWeb.get().uri("/sync/empleados/estado").retrieve()
                .body(EstadoSincronizacionDTO.class);
    }

    @Override
    public SincronizacionResultadoDTO sincronizarManual(String json) {
        return clienteWeb.post().uri("/sync/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .retrieve()
                .body(SincronizacionResultadoDTO.class);
    }

    @Override
    public SincronizacionResultadoDTO sincronizarDesdeFuente() {
        return clienteWeb.post().uri("/sync/empleados/desde-fuente").retrieve()
                .body(SincronizacionResultadoDTO.class);
    }
}
