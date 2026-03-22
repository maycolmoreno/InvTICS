package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.servicios.MantenimientoManualService;
import com.uisrael.gestionactivosapi.presentacion.dto.request.ImagenMantenimientoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.MantenimientoManualRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoManualResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mantenimiento")
@RequiredArgsConstructor
public class MantenimientoManualControlador {

    private final MantenimientoManualService mantenimientoService;

    @GetMapping
    public List<MantenimientoManualResponseDTO> listarTodos() {
        return mantenimientoService.listarTodos();
    }

    @GetMapping("/{id}")
    public MantenimientoManualResponseDTO obtenerDetalle(@PathVariable Integer id) {
        return mantenimientoService.obtenerDetalle(id);
    }

    @GetMapping("/historial/{equipoId}")
    public List<MantenimientoManualResponseDTO> historial(@PathVariable Integer equipoId) {
        return mantenimientoService.obtenerHistorial(equipoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MantenimientoManualResponseDTO crear(@Valid @RequestBody MantenimientoManualRequestDTO request,
            Principal principal) {
        return mantenimientoService.crear(request, principal.getName());
    }

    @PostMapping("/{id}/imagenes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void guardarImagenes(@PathVariable Integer id, @RequestBody List<ImagenMantenimientoRequestDTO> imagenes) {
        mantenimientoService.guardarImagenes(id, imagenes);
    }

    @PostMapping("/cerrar/{id}")
    public MantenimientoManualResponseDTO cerrar(@PathVariable Integer id) {
        return mantenimientoService.cerrar(id);
    }
}
