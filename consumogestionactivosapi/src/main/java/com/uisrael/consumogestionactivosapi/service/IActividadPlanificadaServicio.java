package com.uisrael.consumogestionactivosapi.service;

import java.time.LocalDate;
import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ActividadPlanificadaRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.CambiarEstadoActividadRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.ActividadPlanificadaResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MetricasCumplimientoResponseDTO;

public interface IActividadPlanificadaServicio {

    List<ActividadPlanificadaResponseDTO> listarTodas();

    ActividadPlanificadaResponseDTO obtenerPorId(Long id);

    ActividadPlanificadaResponseDTO crear(ActividadPlanificadaRequestDTO request);

    ActividadPlanificadaResponseDTO actualizar(Long id, ActividadPlanificadaRequestDTO request);

    ActividadPlanificadaResponseDTO cambiarEstado(Long id, CambiarEstadoActividadRequestDTO request);

    List<ActividadPlanificadaResponseDTO> listarPorTecnico(Integer tecnicoId);

    List<ActividadPlanificadaResponseDTO> listarPorTecnicoYEstado(Integer tecnicoId, String estado);

    List<ActividadPlanificadaResponseDTO> listarPorTecnicoYRango(Integer tecnicoId, LocalDate desde, LocalDate hasta);

    MetricasCumplimientoResponseDTO obtenerMetricasTecnico(Integer tecnicoId, String periodo);

    List<MetricasCumplimientoResponseDTO> obtenerMetricasGlobales(String periodo);
}
