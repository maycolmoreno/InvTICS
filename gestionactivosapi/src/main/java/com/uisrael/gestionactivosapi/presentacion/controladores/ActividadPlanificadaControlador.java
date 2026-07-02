package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IActividadPlanificadaUseCase;
import com.uisrael.gestionactivosapi.presentacion.dto.request.ActividadPlanificadaRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CambiarEstadoActividadRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.ActividadPlanificadaResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MetricasCumplimientoResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/actividades-planificadas")
@RequiredArgsConstructor
public class ActividadPlanificadaControlador {

    private final IActividadPlanificadaUseCase actividadService;

    // ==================== CRUD ====================

    @GetMapping
    public List<ActividadPlanificadaResponseDTO> listarTodas() {
        return actividadService.listarTodas();
    }

    @GetMapping("/{id}")
    public ActividadPlanificadaResponseDTO obtenerPorId(@PathVariable Long id) {
        return actividadService.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActividadPlanificadaResponseDTO crear(@Valid @RequestBody ActividadPlanificadaRequestDTO request) {
        return actividadService.crear(request);
    }

    @PutMapping("/{id}")
    public ActividadPlanificadaResponseDTO actualizar(
            @PathVariable Long id,
            @RequestBody ActividadPlanificadaRequestDTO request) {
        return actividadService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public ActividadPlanificadaResponseDTO cambiarEstado(
            @PathVariable Long id,
            @RequestBody CambiarEstadoActividadRequestDTO request) {
        return actividadService.cambiarEstado(id, request);
    }

    // ==================== CONSULTAS POR TÉCNICO ====================

    @GetMapping("/tecnico/{tecnicoId}")
    public List<ActividadPlanificadaResponseDTO> listarPorTecnico(@PathVariable Integer tecnicoId) {
        return actividadService.listarPorTecnico(tecnicoId);
    }

    @GetMapping("/tecnico/{tecnicoId}/estado/{estado}")
    public List<ActividadPlanificadaResponseDTO> listarPorTecnicoYEstado(
            @PathVariable Integer tecnicoId,
            @PathVariable String estado) {
        return actividadService.listarPorTecnicoYEstado(tecnicoId, estado);
    }

    @GetMapping("/tecnico/{tecnicoId}/rango")
    public List<ActividadPlanificadaResponseDTO> listarPorTecnicoYRango(
            @PathVariable Integer tecnicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return actividadService.listarPorTecnicoYRango(tecnicoId, desde, hasta);
    }

    // ==================== MÉTRICAS ====================

    @GetMapping("/metricas/tecnico/{tecnicoId}")
    public MetricasCumplimientoResponseDTO metricasTecnico(
            @PathVariable Integer tecnicoId,
            @RequestParam(defaultValue = "MENSUAL") String periodo) {
        return actividadService.obtenerMetricasTecnico(tecnicoId, periodo);
    }

    @GetMapping("/metricas/global")
    public List<MetricasCumplimientoResponseDTO> metricasGlobales(
            @RequestParam(defaultValue = "MENSUAL") String periodo) {
        return actividadService.obtenerMetricasGlobales(periodo);
    }

}
