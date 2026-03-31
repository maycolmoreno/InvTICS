package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.NotificacionResponseDTO;
import com.uisrael.consumogestionactivosapi.service.INotificacionServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacionServicioImpl implements INotificacionServicio {

    private final RestClient clienteWeb;

    @Override
    public List<NotificacionResponseDTO> listar() {
        return clienteWeb.get()
                .uri("/notificaciones")
                .retrieve()
                .body(new ParameterizedTypeReference<List<NotificacionResponseDTO>>() {});
    }

    @Override
    public long contarNoLeidas() {
        Map<String, Long> resp = clienteWeb.get()
                .uri("/notificaciones/count")
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Long>>() {});
        return resp != null ? resp.getOrDefault("count", 0L) : 0L;
    }

    @Override
    public void marcarLeida(Long idNotificacion) {
        try {
            clienteWeb.post()
                    .uri("/notificaciones/{id}/leer", idNotificacion)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }
}
