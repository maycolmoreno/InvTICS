package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerChecklistPorCategoriaUseCase;
import com.uisrael.gestionactivosapi.dominio.repositorios.IActividadChecklistRepository;
import com.uisrael.gestionactivosapi.presentacion.dto.response.ActividadChecklistResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/actividades-checklist")
@RequiredArgsConstructor
public class ActividadChecklistControlador {

    private final IActividadChecklistRepository actividadChecklistRepository;
    private final IObtenerChecklistPorCategoriaUseCase obtenerChecklistPorCategoriaUseCase;

    @GetMapping
    public List<ActividadChecklistResponseDTO> listarActivas() {
        return actividadChecklistRepository.listarActivas().stream()
                .map(act -> ActividadChecklistResponseDTO.builder()
                        .idActividad(act.getIdActividad())
                        .nombre(act.getNombre())
                        .categoria(act.getCategoria())
                        .orden(act.getOrden())
                        .estado(act.isEstado())
                        .build())
                .toList();
    }

    @GetMapping("/categoria/{idCategoria}")
    public List<ActividadChecklistResponseDTO> listarPorCategoria(@PathVariable Integer idCategoria) {
        return obtenerChecklistPorCategoriaUseCase.ejecutar(idCategoria).stream()
                .map(act -> ActividadChecklistResponseDTO.builder()
                        .idActividad(act.getIdActividad())
                        .nombre(act.getNombre())
                        .categoria(act.getCategoria())
                        .orden(act.getOrden())
                        .estado(act.isEstado())
                        .build())
                .toList();
    }
}
