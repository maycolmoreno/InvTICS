package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.MantenimientoProgramadoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoProgramadoResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoProgramadoServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MantenimientoProgramadoServicioImpl implements IMantenimientoProgramadoServicio {

    private final WebClient clienteWeb;

    @Override
    public List<MantenimientoProgramadoResponseDTO> listarTodos() {
        return clienteWeb.get()
                .uri("/mantenimiento/programado")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<MantenimientoProgramadoResponseDTO>>() {})
                .block();
    }

    @Override
    public List<MantenimientoProgramadoResponseDTO> listarVencidosYProximos() {
        return clienteWeb.get()
                .uri("/mantenimiento/programado/vencidos-proximos")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<MantenimientoProgramadoResponseDTO>>() {})
                .block();
    }

    @Override
    public MantenimientoProgramadoResponseDTO guardar(MantenimientoProgramadoRequestDTO request) {
        try {
            return clienteWeb.post()
                    .uri("/mantenimiento/programado")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(MantenimientoProgramadoResponseDTO.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }

    @Override
    public void desactivar(Long idProgramado) {
        try {
            clienteWeb.post()
                    .uri("/mantenimiento/programado/desactivar/{id}", idProgramado)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }
}
