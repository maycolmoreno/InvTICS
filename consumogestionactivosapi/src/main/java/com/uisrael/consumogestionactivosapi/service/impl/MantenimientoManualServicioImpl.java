package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ImagenMantenimientoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.MantenimientoManualRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoManualResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoManualServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MantenimientoManualServicioImpl implements IMantenimientoManualServicio {

    private final WebClient clienteWeb;

    @Override
    public MantenimientoManualResponseDTO crear(MantenimientoManualRequestDTO request) {
        try {
            return clienteWeb.post()
                    .uri("/mantenimiento")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(MantenimientoManualResponseDTO.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }

    @Override
    public void guardarImagenes(Integer idMantenimiento, List<ImagenMantenimientoRequestDTO> imagenes) {
        try {
            clienteWeb.post()
                    .uri("/mantenimiento/{id}/imagenes", idMantenimiento)
                    .bodyValue(imagenes)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }

    @Override
    public List<MantenimientoManualResponseDTO> listarTodos() {
        return clienteWeb.get()
                .uri("/mantenimiento")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<MantenimientoManualResponseDTO>>() {})
                .block();
    }

    @Override
    public MantenimientoManualResponseDTO obtenerDetalle(Integer id) {
        return clienteWeb.get()
                .uri("/mantenimiento/{id}", id)
                .retrieve()
                .bodyToMono(MantenimientoManualResponseDTO.class)
                .block();
    }

    @Override
    public List<MantenimientoManualResponseDTO> obtenerHistorial(Integer equipoId) {
        return clienteWeb.get()
                .uri("/mantenimiento/historial/{equipoId}", equipoId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<MantenimientoManualResponseDTO>>() {})
                .block();
    }

    @Override
    public MantenimientoManualResponseDTO cerrar(Integer id) {
        return clienteWeb.post()
                .uri("/mantenimiento/cerrar/{id}", id)
                .retrieve()
                .bodyToMono(MantenimientoManualResponseDTO.class)
                .block();
    }
}
