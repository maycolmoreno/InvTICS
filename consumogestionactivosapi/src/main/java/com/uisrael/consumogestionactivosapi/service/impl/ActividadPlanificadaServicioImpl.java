package com.uisrael.consumogestionactivosapi.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ActividadPlanificadaRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.CambiarEstadoActividadRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.ActividadPlanificadaResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MetricasCumplimientoResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IActividadPlanificadaServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActividadPlanificadaServicioImpl implements IActividadPlanificadaServicio {

    private final RestClient clienteWeb;

    @Override
    public List<ActividadPlanificadaResponseDTO> listarTodas() {
        return clienteWeb.get()
                .uri("/actividades-planificadas")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ActividadPlanificadaResponseDTO>>() {});
    }

    @Override
    public ActividadPlanificadaResponseDTO obtenerPorId(Long id) {
        return clienteWeb.get()
                .uri("/actividades-planificadas/{id}", id)
                .retrieve()
                .body(ActividadPlanificadaResponseDTO.class);
    }

    @Override
    public ActividadPlanificadaResponseDTO crear(ActividadPlanificadaRequestDTO request) {
        try {
            return clienteWeb.post()
                    .uri("/actividades-planificadas")
                    .body(request)
                    .retrieve()
                    .body(ActividadPlanificadaResponseDTO.class);
        } catch (RestClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }

    @Override
    public ActividadPlanificadaResponseDTO actualizar(Long id, ActividadPlanificadaRequestDTO request) {
        try {
            return clienteWeb.put()
                    .uri("/actividades-planificadas/{id}", id)
                    .body(request)
                    .retrieve()
                    .body(ActividadPlanificadaResponseDTO.class);
        } catch (RestClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }

    @Override
    public ActividadPlanificadaResponseDTO cambiarEstado(Long id, CambiarEstadoActividadRequestDTO request) {
        try {
            return clienteWeb.patch()
                    .uri("/actividades-planificadas/{id}/estado", id)
                    .body(request)
                    .retrieve()
                    .body(ActividadPlanificadaResponseDTO.class);
        } catch (RestClientResponseException ex) {
            throw WebClientHelper.manejarError(ex);
        }
    }

    @Override
    public List<ActividadPlanificadaResponseDTO> listarPorTecnico(Integer tecnicoId) {
        return clienteWeb.get()
                .uri("/actividades-planificadas/tecnico/{tecnicoId}", tecnicoId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ActividadPlanificadaResponseDTO>>() {});
    }

    @Override
    public List<ActividadPlanificadaResponseDTO> listarPorTecnicoYEstado(Integer tecnicoId, String estado) {
        return clienteWeb.get()
                .uri("/actividades-planificadas/tecnico/{tecnicoId}/estado/{estado}", tecnicoId, estado)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ActividadPlanificadaResponseDTO>>() {});
    }

    @Override
    public List<ActividadPlanificadaResponseDTO> listarPorTecnicoYRango(Integer tecnicoId, LocalDate desde, LocalDate hasta) {
        return clienteWeb.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/actividades-planificadas/tecnico/{tecnicoId}/rango")
                        .queryParam("desde", desde.toString())
                        .queryParam("hasta", hasta.toString())
                        .build(tecnicoId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ActividadPlanificadaResponseDTO>>() {});
    }

    @Override
    public MetricasCumplimientoResponseDTO obtenerMetricasTecnico(Integer tecnicoId, String periodo) {
        return clienteWeb.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/actividades-planificadas/metricas/tecnico/{tecnicoId}")
                        .queryParam("periodo", periodo)
                        .build(tecnicoId))
                .retrieve()
                .body(MetricasCumplimientoResponseDTO.class);
    }

    @Override
    public List<MetricasCumplimientoResponseDTO> obtenerMetricasGlobales(String periodo) {
        return clienteWeb.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/actividades-planificadas/metricas/global")
                        .queryParam("periodo", periodo)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<MetricasCumplimientoResponseDTO>>() {});
    }
}
