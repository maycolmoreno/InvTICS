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

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRolesUseCase;
import com.uisrael.gestionactivosapi.presentacion.dto.request.RolesRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.RolesResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.IRolesDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
public class RolesControlador {

	private final IRolesUseCase rolesUseCase;

	private final IRolesDtoMapper mapper;

	public RolesControlador(IRolesUseCase rolesUseCase, IRolesDtoMapper mapper) {
		this.rolesUseCase = rolesUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public RolesResponseDTO crear(@Valid @RequestBody RolesRequestDTO request) {
		return mapper.toResponseDto(rolesUseCase.crear(mapper.toDomain(request)));
	}

	@PutMapping
	public RolesResponseDTO actualizar(@Valid @RequestBody RolesRequestDTO request) {
		return mapper.toResponseDto(rolesUseCase.actualizar(mapper.toDomain(request)));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable int id) {
		rolesUseCase.eliminar(id);
	}

	@GetMapping
	public List<RolesResponseDTO> listar() {
		return rolesUseCase.listar().stream().map(mapper::toResponseDto).toList();
	}

	@GetMapping("/{id}")
	public RolesResponseDTO obtener(@PathVariable int id) {
		return mapper.toResponseDto(rolesUseCase.obtenerPorId(id));
	}
}

