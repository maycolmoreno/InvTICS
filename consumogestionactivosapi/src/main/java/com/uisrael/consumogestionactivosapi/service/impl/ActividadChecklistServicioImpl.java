package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.ActividadChecklistResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IActividadChecklistServicio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActividadChecklistServicioImpl implements IActividadChecklistServicio {

    private final WebClient clienteWeb;

    @Override
    public List<ActividadChecklistResponseDTO> listarActivas() {
        return clienteWeb.get()
                .uri("/actividades-checklist")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ActividadChecklistResponseDTO>>() {})
                .block();
    }
}
