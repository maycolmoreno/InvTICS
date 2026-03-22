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

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IUsuariosUseCase;
import com.uisrael.gestionactivosapi.presentacion.dto.request.UsuariosRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.UsuariosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.IUsuariosDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosControlador {

	private final IUsuariosUseCase usuariosUseCase;

	private final IUsuariosDtoMapper mapper;

	public UsuariosControlador(IUsuariosUseCase usuariosUseCase, IUsuariosDtoMapper mapper) {
		this.usuariosUseCase = usuariosUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public UsuariosResponseDTO crear(@Valid @RequestBody UsuariosRequestDTO request) {
		return mapper.toResponseDto(usuariosUseCase.crear(mapper.toDomain(request)));
	}

	@GetMapping
	public List<UsuariosResponseDTO> listar() {
		return usuariosUseCase.listar().stream().map(mapper::toResponseDto).toList();
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable int id) {
		usuariosUseCase.eliminar(id);
	}

	@GetMapping("/{id}")
	public UsuariosResponseDTO obtener(@PathVariable int id) {
		return mapper.toResponseDto(usuariosUseCase.obtenerPorId(id));
	}

	@PutMapping
	public UsuariosResponseDTO actualizar(@Valid @RequestBody UsuariosRequestDTO request) {
		return mapper.toResponseDto(usuariosUseCase.actualizar(mapper.toDomain(request)));
	}
}

