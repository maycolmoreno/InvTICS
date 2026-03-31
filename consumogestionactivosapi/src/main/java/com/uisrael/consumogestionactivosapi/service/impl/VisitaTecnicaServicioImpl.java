package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.VisitaCustodioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.VisitaEquipoResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IVisitaTecnicaServicio;

@Service
public class VisitaTecnicaServicioImpl implements IVisitaTecnicaServicio {

    private final RestClient clienteWeb;

    public VisitaTecnicaServicioImpl(RestClient clienteWeb) {
        this.clienteWeb = clienteWeb;
    }

    @Override
    public List<VisitaEquipoResponseDTO> obtenerEquipos(Long ubicacionId, Long custodioId) {
        return clienteWeb.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/visita/equipos").queryParam("ubicacionId", ubicacionId);
                    if (custodioId != null) {
                        uriBuilder.queryParam("custodioId", custodioId);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<List<VisitaEquipoResponseDTO>>() {});
    }

    @Override
    public List<VisitaCustodioResponseDTO> obtenerCustodios(Long ubicacionId) {
        return clienteWeb.get()
                .uri(uriBuilder -> uriBuilder.path("/visita/custodios")
                        .queryParam("ubicacionId", ubicacionId)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<VisitaCustodioResponseDTO>>() {});
    }
}
