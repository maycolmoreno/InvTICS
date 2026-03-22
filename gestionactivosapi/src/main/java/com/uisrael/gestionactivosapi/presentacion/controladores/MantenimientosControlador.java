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

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMantenimientosUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.presentacion.dto.request.MantenimientosRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.IMantenimientosDtoMapper;

@RestController
@RequestMapping("/api/mantenimientos")
public class MantenimientosControlador {

    private final IMantenimientosUseCase mantenimientosUseCase;
    private final IMantenimientosDtoMapper mapper;

    public MantenimientosControlador(IMantenimientosUseCase mantenimientosUseCase,
            IMantenimientosDtoMapper mapper) {
        this.mantenimientosUseCase = mantenimientosUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MantenimientosResponseDTO crear(@RequestBody MantenimientosRequestDTO request) {
        Mantenimientos dominio = mapper.toDomain(request);
        Mantenimientos creado = mantenimientosUseCase.crear(dominio);
        return mapper.toResponseDto(creado);
    }

    @GetMapping
    public List<MantenimientosResponseDTO> listar() {
        return mantenimientosUseCase.listar().stream().map(mapper::toResponseDto).toList();
    }

    @GetMapping("/{id}")
    public MantenimientosResponseDTO obtenerPorId(@PathVariable int id) {
        return mapper.toResponseDto(mantenimientosUseCase.obtenerPorId(id));
    }
}
