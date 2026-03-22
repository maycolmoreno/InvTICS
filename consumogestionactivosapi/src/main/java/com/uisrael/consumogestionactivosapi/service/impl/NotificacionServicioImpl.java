package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.NotificacionResponseDTO;
import com.uisrael.consumogestionactivosapi.service.INotificacionServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacionServicioImpl implements INotificacionServicio {

    private final WebClient clienteWeb;

    @Override
    public List<NotificacionResponseDTO> listar() {
        return clienteWeb.get()
                .uri("/notificaciones")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<NotificacionResponseDTO>>() {})
                .block();
    }

    @Override
    public long contarNoLeidas() {
        Map<String, Long> resp = clienteWeb.get()
                .uri("/notificaciones/count")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Long>>() {})
                .block();
        return resp != null ? resp.getOrDefault("count", 0L) : 0L;
    }

    @Override
    public void marcarLeida(Long idNotificacion) {
        try {
            clienteWeb.post()
                    .uri("/notificaciones/{id}/leer", idNotificacion)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }
}
