package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.time.LocalDate;
import java.util.List;

import com.uisrael.gestionactivosapi.presentacion.dto.request.ActividadPlanificadaRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CambiarEstadoActividadRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.ActividadPlanificadaResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MetricasCumplimientoResponseDTO;

public interface IActividadPlanificadaUseCase {

    ActividadPlanificadaResponseDTO crear(ActividadPlanificadaRequestDTO request);

    ActividadPlanificadaResponseDTO actualizar(Long id, ActividadPlanificadaRequestDTO request);

    ActividadPlanificadaResponseDTO cambiarEstado(Long id, CambiarEstadoActividadRequestDTO request);

    List<ActividadPlanificadaResponseDTO> listarTodas();

    List<ActividadPlanificadaResponseDTO> listarPorTecnico(Integer tecnicoId);

    List<ActividadPlanificadaResponseDTO> listarPorTecnicoYEstado(Integer tecnicoId, String estado);

    List<ActividadPlanificadaResponseDTO> listarPorTecnicoYRango(Integer tecnicoId, LocalDate desde, LocalDate hasta);

    ActividadPlanificadaResponseDTO obtenerPorId(Long id);

    MetricasCumplimientoResponseDTO obtenerMetricasTecnico(Integer tecnicoId, String periodo);

    List<MetricasCumplimientoResponseDTO> obtenerMetricasGlobales(String periodo);
}
