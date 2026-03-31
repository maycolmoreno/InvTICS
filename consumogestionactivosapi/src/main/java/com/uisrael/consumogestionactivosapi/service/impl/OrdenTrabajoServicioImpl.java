package com.uisrael.consumogestionactivosapi.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.OrdenCrearRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.OrdenGuardarRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.OrdenCrearResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.OrdenTrabajoResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IOrdenTrabajoServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

@Service
public class OrdenTrabajoServicioImpl implements IOrdenTrabajoServicio {

    private final RestClient clienteWeb;

    public OrdenTrabajoServicioImpl(RestClient clienteWeb) {
        this.clienteWeb = clienteWeb;
    }

    @Override
    public OrdenCrearResponseDTO crearOrden(OrdenCrearRequestDTO request) {
        return clienteWeb.post()
                .uri("/orden/crear")
                .body(request)
                .retrieve()
                .body(OrdenCrearResponseDTO.class);
    }

    @Override
    public OrdenTrabajoResponseDTO obtenerOrden(Integer id) {
        return clienteWeb.get()
                .uri(uriBuilder -> uriBuilder.path("/orden/{id}").build(id))
                .retrieve()
                .body(OrdenTrabajoResponseDTO.class);
    }

    @Override
    public void guardarOrden(Integer id, OrdenGuardarRequestDTO request) {
        try {
            clienteWeb.post()
                    .uri(uriBuilder -> uriBuilder.path("/orden/{id}/guardar").build(id))
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }
}
