package com.uisrael.consumogestionactivosapi.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.OrdenCrearRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.OrdenGuardarRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.OrdenCrearResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.OrdenTrabajoResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IOrdenTrabajoServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

@Service
public class OrdenTrabajoServicioImpl implements IOrdenTrabajoServicio {

    private final WebClient clienteWeb;

    public OrdenTrabajoServicioImpl(WebClient clienteWeb) {
        this.clienteWeb = clienteWeb;
    }

    @Override
    public OrdenCrearResponseDTO crearOrden(OrdenCrearRequestDTO request) {
        return clienteWeb.post()
                .uri("/orden/crear")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrdenCrearResponseDTO.class)
                .block();
    }

    @Override
    public OrdenTrabajoResponseDTO obtenerOrden(Integer id) {
        return clienteWeb.get()
                .uri(uriBuilder -> uriBuilder.path("/orden/{id}").build(id))
                .retrieve()
                .bodyToMono(OrdenTrabajoResponseDTO.class)
                .block();
    }

    @Override
    public void guardarOrden(Integer id, OrdenGuardarRequestDTO request) {
        try {
            clienteWeb.post()
                    .uri(uriBuilder -> uriBuilder.path("/orden/{id}/guardar").build(id))
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }
}
