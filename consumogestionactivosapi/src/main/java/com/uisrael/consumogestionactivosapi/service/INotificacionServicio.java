package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.NotificacionResponseDTO;

public interface INotificacionServicio {

    List<NotificacionResponseDTO> listar();

    long contarNoLeidas();

    void marcarLeida(Long idNotificacion);
}
