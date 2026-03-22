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

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICargosUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Cargos;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CargosRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CargosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.ICargoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cargos")
public class CargoControlador {

	private final ICargosUseCase cargoUseCase;

	private final ICargoDtoMapper mapper;


	public CargoControlador(ICargosUseCase cargoUseCase, ICargoDtoMapper mapper) {
		this.cargoUseCase = cargoUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<?> crear(@Valid @RequestBody CargosRequestDTO request) {

		// âœ… si ya existe, no intentes guardar
		if (cargoUseCase.nombreExiste(request.getNombre().trim())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un cargo con ese nombre");
		}

		CargosResponseDTO creado = mapper.toResponseDto(cargoUseCase.crear(mapper.toDomain(request)));

		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}

	@GetMapping
	public List<CargosResponseDTO> listar() {
		return cargoUseCase.listar().stream().map(mapper::toResponseDto).toList();

	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @Valid @RequestBody CargosRequestDTO request) {

		if (cargoUseCase.nombreExisteParaOtro(request.getNombre().trim(), id)) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe otro cargo con ese nombre");
		}

		Cargos actualizado = cargoUseCase.actualizar(id, mapper.toDomain(request));
		return ResponseEntity.ok(mapper.toResponseDto(actualizado));
	}

	@PutMapping("/estado/{id}")
	public ResponseEntity<CargosResponseDTO> actualizarEstado(@PathVariable int id,
			@RequestBody java.util.Map<String, Boolean> body) {

		boolean estado = Boolean.TRUE.equals(body.get("estado"));
		Cargos actualizado = cargoUseCase.actualizarEstado(id, estado);
		return ResponseEntity.ok(mapper.toResponseDto(actualizado));
	}

	@GetMapping("/{id}")
	public ResponseEntity<CargosResponseDTO> obtenerPorId(@PathVariable int id) {
		Cargos cargo = cargoUseCase.obtenerPorId(id);
		return ResponseEntity.ok(mapper.toResponseDto(cargo));
	}

	@GetMapping("/existe-nombre")
	public ResponseEntity<Boolean> existeNombre(@RequestParam String nombre,
			@RequestParam(required = false) Integer id) {
		boolean existe = (id == null) ? cargoUseCase.nombreExiste(nombre)
				: cargoUseCase.nombreExisteParaOtro(nombre, id);

		return ResponseEntity.ok(existe);
	}

}

