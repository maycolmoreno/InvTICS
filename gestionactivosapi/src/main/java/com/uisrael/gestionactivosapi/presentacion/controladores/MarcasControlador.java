package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMarcasUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;
import com.uisrael.gestionactivosapi.presentacion.dto.request.MarcasRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MarcasResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.IMarcasDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/marcas")
public class MarcasControlador {

    private final IMarcasUseCase marcasUseCase;
    private final IMarcasDtoMapper mapper;

    public MarcasControlador(IMarcasUseCase marcasUseCase, IMarcasDtoMapper mapper) {
        this.marcasUseCase = marcasUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public MarcasResponseDTO crear(@Valid @RequestBody MarcasRequestDTO request) {
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio");
        }
        return mapper.toResponseDto(marcasUseCase.crear(mapper.toDomain(request)));
    }

    @GetMapping
    public List<MarcasResponseDTO> listar() {
        return marcasUseCase.listar().stream().map(mapper::toResponseDto).toList();
    }

    @PutMapping("/{id}")
	public ResponseEntity<MarcasResponseDTO> actualizar(@PathVariable int id,
			@Valid @RequestBody MarcasRequestDTO request) {
		if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
			throw new IllegalArgumentException("El nombre de la marca es obligatorio");
		}
		Marcas actualizado = marcasUseCase.actualizar(id, mapper.toDomain(request));
		return ResponseEntity.ok(mapper.toResponseDto(actualizado));
	}

    @DeleteMapping("/{id}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable int id) {
		marcasUseCase.eliminar(id);
    }

    @GetMapping("/{id}")
	public MarcasResponseDTO obtenerPorId(@PathVariable int id) {
		return mapper.toResponseDto(marcasUseCase.obtenerPorId(id));
	}
}
