package com.uisrael.consumogestionactivosapi.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.HistorialCompletoDTO;
import com.uisrael.consumogestionactivosapi.service.IHistorialEquipoServicio;

@Service
public class HistorialEquipoServicioImpl implements IHistorialEquipoServicio {

    private final RestClient clienteWeb;

    public HistorialEquipoServicioImpl(RestClient clienteWeb) {
        this.clienteWeb = clienteWeb;
    }

    @Override
    public HistorialCompletoDTO obtenerHistorial(Long equipoId) {
        return clienteWeb.get()
                .uri(uriBuilder -> uriBuilder.path("/historial/{id}").build(equipoId))
                .retrieve()
                .body(HistorialCompletoDTO.class);
    }
}
