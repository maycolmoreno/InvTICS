package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.MantenimientoProgramadoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoProgramadoResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoProgramadoServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MantenimientoProgramadoServicioImpl implements IMantenimientoProgramadoServicio {

    private final RestClient clienteWeb;

    @Override
    public List<MantenimientoProgramadoResponseDTO> listarTodos() {
        return clienteWeb.get()
                .uri("/mantenimiento/programado")
                .retrieve()
                .body(new ParameterizedTypeReference<List<MantenimientoProgramadoResponseDTO>>() {});
    }

    @Override
    public List<MantenimientoProgramadoResponseDTO> listarVencidosYProximos() {
        return clienteWeb.get()
                .uri("/mantenimiento/programado/vencidos-proximos")
                .retrieve()
                .body(new ParameterizedTypeReference<List<MantenimientoProgramadoResponseDTO>>() {});
    }

    @Override
    public MantenimientoProgramadoResponseDTO obtenerPorEquipo(Integer equipoId) {
        try {
            return clienteWeb.get()
                    .uri("/mantenimiento/programado/equipo/{equipoId}", equipoId)
                    .retrieve()
                    .body(MantenimientoProgramadoResponseDTO.class);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw WebClientHelper.manejarError(ex);
        }
    }

    @Override
    public MantenimientoProgramadoResponseDTO guardar(MantenimientoProgramadoRequestDTO request) {
        try {
            return clienteWeb.post()
                    .uri("/mantenimiento/programado")
                    .body(request)
                    .retrieve()
                    .body(MantenimientoProgramadoResponseDTO.class);
        } catch (RestClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }

    @Override
    public void desactivar(Long idProgramado) {
        try {
            clienteWeb.post()
                    .uri("/mantenimiento/programado/desactivar/{id}", idProgramado)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }
}
