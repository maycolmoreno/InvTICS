package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.infraestructura.servicios.MantenimientoProgramadoService;
import com.uisrael.gestionactivosapi.presentacion.dto.request.MantenimientoProgramadoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoProgramadoResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mantenimiento/programado")
@RequiredArgsConstructor
public class MantenimientoProgramadoControlador {

    private final MantenimientoProgramadoService programadoService;

    @GetMapping
    public List<MantenimientoProgramadoResponseDTO> listarTodos() {
        return programadoService.listarTodos();
    }

    @GetMapping("/vencidos-proximos")
    public List<MantenimientoProgramadoResponseDTO> vencidosYProximos() {
        return programadoService.obtenerVencidosYProximos();
    }

    @GetMapping("/equipo/{equipoId}")
    public MantenimientoProgramadoResponseDTO obtenerPorEquipo(@PathVariable Integer equipoId) {
        return programadoService.obtenerPorEquipo(equipoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MantenimientoProgramadoResponseDTO guardar(@RequestBody MantenimientoProgramadoRequestDTO request) {
        return programadoService.programar(request);
    }

    @PostMapping("/desactivar/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        programadoService.desactivar(id);
    }
}
