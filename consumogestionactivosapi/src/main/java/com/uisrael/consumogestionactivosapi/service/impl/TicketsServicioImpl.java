package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.TicketRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.TicketResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ITicketsServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

@Service
public class TicketsServicioImpl implements ITicketsServicio {

    private final WebClient clienteWeb;

    public TicketsServicioImpl(WebClient clienteWeb) {
        this.clienteWeb = clienteWeb;
    }

    @Override
    public List<TicketResponseDTO> listar(String estado, Integer idEquipo, String odooTicketId) {
        return clienteWeb.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/tickets");
                    if (estado != null && !estado.isBlank()) {
                        uriBuilder.queryParam("estado", estado);
                    }
                    if (idEquipo != null) {
                        uriBuilder.queryParam("idEquipo", idEquipo);
                    }
                    if (odooTicketId != null && !odooTicketId.isBlank()) {
                        uriBuilder.queryParam("odooTicketId", odooTicketId);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToFlux(TicketResponseDTO.class)
                .collectList()
                .block();
    }

    @Override
    public TicketResponseDTO crear(TicketRequestDTO dto) {
        try {
            return clienteWeb.post()
                    .uri("/tickets")
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(TicketResponseDTO.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }

    @Override
    public TicketResponseDTO obtenerPorId(Integer idTicket) {
        return clienteWeb.get()
                .uri("/tickets/{idTicket}", idTicket)
                .retrieve()
                .bodyToMono(TicketResponseDTO.class)
                .block();
    }

    @Override
    public TicketResponseDTO asignar(Integer idTicket, Integer idTecnico) {
        try {
            return clienteWeb.put()
                    .uri("/tickets/{idTicket}/asignar/{idTecnico}", idTicket, idTecnico)
                    .retrieve()
                    .bodyToMono(TicketResponseDTO.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }

    @Override
    public TicketResponseDTO cerrarYCrearMantenimiento(Integer idTicket) {
        try {
            return clienteWeb.post()
                    .uri("/tickets/{idTicket}/cerrar-y-crear-mantenimiento", idTicket)
                    .retrieve()
                    .bodyToMono(TicketResponseDTO.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }
}
