package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerChecklistPorCategoriaUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.ActividadChecklist;
import com.uisrael.gestionactivosapi.presentacion.dto.request.ActividadChecklistRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.ActividadChecklistResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/actividades-checklist")
@RequiredArgsConstructor
public class ActividadChecklistControlador {

    private final IObtenerChecklistPorCategoriaUseCase checklistUseCase;

    @GetMapping
    public List<ActividadChecklistResponseDTO> listarActivas() {
        return checklistUseCase.listarActivas().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ActividadChecklistResponseDTO obtenerPorId(@PathVariable Integer id) {
        return toResponse(checklistUseCase.obtenerPorId(id));
    }

    @GetMapping("/categoria/{idCategoria}")
    public List<ActividadChecklistResponseDTO> listarPorCategoria(@PathVariable Integer idCategoria) {
        return checklistUseCase.ejecutar(idCategoria).stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActividadChecklistResponseDTO crear(@RequestBody ActividadChecklistRequestDTO request) {
        ActividadChecklist actividad = toDomain(request);
        return toResponse(checklistUseCase.crear(actividad));
    }

    @PutMapping("/{id}")
    public ActividadChecklistResponseDTO actualizar(@PathVariable Integer id,
            @RequestBody ActividadChecklistRequestDTO request) {
        ActividadChecklist actividad = toDomain(request);
        return toResponse(checklistUseCase.actualizar(id, actividad));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        checklistUseCase.eliminar(id);
    }

    private ActividadChecklistResponseDTO toResponse(ActividadChecklist act) {
        return ActividadChecklistResponseDTO.builder()
                .idActividad(act.getIdActividad())
                .nombre(act.getNombre())
                .categorias(act.getCategoria() != null ? List.of(act.getCategoria()) : List.of())
                .orden(act.getOrden())
                .estado(act.isEstado())
                .build();
    }

    private ActividadChecklist toDomain(ActividadChecklistRequestDTO dto) {
        ActividadChecklist act = new ActividadChecklist();
        act.setNombre(dto.nombre());
        act.setCategoria(dto.categoria());
        act.setOrden(dto.orden());
        act.setEstado(dto.estado() != null ? dto.estado() : true);
        return act;
    }
}
