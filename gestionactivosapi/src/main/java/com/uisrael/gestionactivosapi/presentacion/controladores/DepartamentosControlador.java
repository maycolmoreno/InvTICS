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

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IDepartamentosUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import com.uisrael.gestionactivosapi.presentacion.dto.request.DepartamentosRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.DepartamentosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.IDepartamentosDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentosControlador {

	private final IDepartamentosUseCase departamentoUseCase;

	private final IDepartamentosDtoMapper mapper;

	public DepartamentosControlador(IDepartamentosUseCase departamentoUseCase, IDepartamentosDtoMapper mapper) {
		this.departamentoUseCase = departamentoUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<?> crear(@Valid @RequestBody DepartamentosRequestDTO request) {

		// âœ… si ya existe, no intentes guardar
		if (departamentoUseCase.nombreExiste(request.getNombre().trim())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un departamento con ese nombre");
		}

		DepartamentosResponseDTO creado = mapper.toResponseDto(departamentoUseCase.crear(mapper.toDomain(request)));

		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}

	@GetMapping
	public List<DepartamentosResponseDTO> listar() {
		return departamentoUseCase.listar().stream().map(mapper::toResponseDto).toList();

	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @Valid @RequestBody DepartamentosRequestDTO request) {

		if (departamentoUseCase.nombreExisteParaOtro(request.getNombre().trim(), id)) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe otro departamento con ese nombre");
		}

		Departamentos actualizado = departamentoUseCase.actualizar(id, mapper.toDomain(request));
		return ResponseEntity.ok(mapper.toResponseDto(actualizado));
	}

	@PutMapping("/estado/{id}")
	public ResponseEntity<DepartamentosResponseDTO> actualizarEstado(@PathVariable int id,
			@RequestBody java.util.Map<String, Boolean> body) {

		boolean estado = Boolean.TRUE.equals(body.get("estado"));
		Departamentos actualizado = departamentoUseCase.actualizarEstado(id, estado);
		return ResponseEntity.ok(mapper.toResponseDto(actualizado));
	}

	@GetMapping("/{id}")
	public ResponseEntity<DepartamentosResponseDTO> obtenerPorId(@PathVariable int id) {
		Departamentos departamento = departamentoUseCase.obtenerPorId(id);
		return ResponseEntity.ok(mapper.toResponseDto(departamento));
	}

	@GetMapping("/existe-nombre")
	public ResponseEntity<Boolean> existeNombre(@RequestParam String nombre,
			@RequestParam(required = false) Integer id) {
		boolean existe = (id == null) ? departamentoUseCase.nombreExiste(nombre)
				: departamentoUseCase.nombreExisteParaOtro(nombre, id);

		return ResponseEntity.ok(existe);
	}
}

