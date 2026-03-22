package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IUbicacionesUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;
import com.uisrael.gestionactivosapi.presentacion.dto.request.UbicacionesRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.UbicacionesResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.IUbicacionesDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ubicaciones")
public class UbicacionesControlador {

	private final IUbicacionesUseCase ubicacionUseCase;

	private final IUbicacionesDtoMapper mapper;

	public UbicacionesControlador(IUbicacionesUseCase ubicacionUseCase, IUbicacionesDtoMapper mapper) {
		this.ubicacionUseCase = ubicacionUseCase;
		this.mapper = mapper;
	}


	@PostMapping
	public ResponseEntity<?> crear(@Valid @RequestBody UbicacionesRequestDTO request) {

		// Si ya existe, no intentes guardar
		if (ubicacionUseCase.nombreExiste(request.getNombre().trim())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe una ubicacion con ese nombre");
		}

		UbicacionesResponseDTO creado = mapper.toResponseDto(ubicacionUseCase.crear(mapper.toDomain(request)));

		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}

	@GetMapping
	public List<UbicacionesResponseDTO> listar() {
		return ubicacionUseCase.listar().stream().map(mapper::toResponseDto).toList();
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @Valid @RequestBody UbicacionesRequestDTO request) {

		if (ubicacionUseCase.nombreExisteParaOtro(request.getNombre().trim(), id)) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe otro departamento con ese nombre");
		}

		Ubicaciones actualizado = ubicacionUseCase.actualizar(id, mapper.toDomain(request));
		return ResponseEntity.ok(mapper.toResponseDto(actualizado));
	}

	@PutMapping("/estado/{id}")
	public ResponseEntity<UbicacionesResponseDTO> actualizarEstado(
	        @PathVariable int id,
	        @RequestBody java.util.Map<String, Boolean> body) {

	    boolean estado = Boolean.TRUE.equals(body.get("estado"));
	    Ubicaciones actualizado = ubicacionUseCase.actualizarEstado(id, estado);
	    return ResponseEntity.ok(mapper.toResponseDto(actualizado));
	}

	@GetMapping("/{id}")
	public ResponseEntity<UbicacionesResponseDTO> obtenerPorId(@PathVariable int id) {
	    Ubicaciones ubicacion = ubicacionUseCase.obtenerPorId(id);
	    return ResponseEntity.ok(mapper.toResponseDto(ubicacion));
	}

	@GetMapping("/existe-nombre")
	public ResponseEntity<Boolean> existeNombre(@RequestParam String nombre,
			@RequestParam(required = false) Integer id) {
		boolean existe = (id == null) ? ubicacionUseCase.nombreExiste(nombre)
				: ubicacionUseCase.nombreExisteParaOtro(nombre, id);

		return ResponseEntity.ok(existe);
	}
}

