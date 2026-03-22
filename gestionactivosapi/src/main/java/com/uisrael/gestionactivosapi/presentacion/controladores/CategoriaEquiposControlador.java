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

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICategoriaEquiposUseCase;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CategoriaEquiposRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CategoriaEquiposResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.ICategoriaEquiposDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias-equipo")
public class CategoriaEquiposControlador {

	private final ICategoriaEquiposUseCase categoriaEquiposUseCase;

	private final ICategoriaEquiposDtoMapper mapper;

	public CategoriaEquiposControlador(ICategoriaEquiposUseCase categoriaEquiposUseCase, ICategoriaEquiposDtoMapper mapper) {
		this.categoriaEquiposUseCase = categoriaEquiposUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public CategoriaEquiposResponseDTO crear(@Valid @RequestBody CategoriaEquiposRequestDTO request) {
		return mapper.toResponseDto(categoriaEquiposUseCase.crear(mapper.toDomain(request)));
	}

	@GetMapping
	public List<CategoriaEquiposResponseDTO> listar() {
		return categoriaEquiposUseCase.listar().stream().map(mapper::toResponseDto).toList();
	}

	@GetMapping("/{id}")
	public CategoriaEquiposResponseDTO obtener(@PathVariable int id) {
		return mapper.toResponseDto(categoriaEquiposUseCase.obtenerPorId(id));
	}

	@PutMapping
	public CategoriaEquiposResponseDTO actualizar(@Valid @RequestBody CategoriaEquiposRequestDTO request) {
		return mapper.toResponseDto(categoriaEquiposUseCase.actualizar(mapper.toDomain(request)));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable int id) {
		categoriaEquiposUseCase.eliminar(id);
	}
}

