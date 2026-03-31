package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ActividadChecklistRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.ActividadChecklistResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IActividadChecklistServicio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActividadChecklistServicioImpl implements IActividadChecklistServicio {

    private final RestClient clienteWeb;

    @Override
    public List<ActividadChecklistResponseDTO> listarActivas() {
        return clienteWeb.get()
                .uri("/actividades-checklist")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ActividadChecklistResponseDTO>>() {});
    }

    @Override
    public ActividadChecklistResponseDTO obtenerPorId(Integer id) {
        try {
            return clienteWeb.get()
                    .uri("/actividades-checklist/{id}", id)
                    .retrieve()
                    .body(ActividadChecklistResponseDTO.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Actividad checklist no encontrada con id: " + id);
            }
            throw e;
        }
    }

    @Override
    public void crear(ActividadChecklistRequestDTO dto) {
        clienteWeb.post()
                .uri("/actividades-checklist")
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void actualizar(Integer id, ActividadChecklistRequestDTO dto) {
        clienteWeb.put()
                .uri("/actividades-checklist/{id}", id)
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void eliminar(Integer id) {
        clienteWeb.delete()
                .uri("/actividades-checklist/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
