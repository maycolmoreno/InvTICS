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

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICustodiosUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CustodiosRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CustodiosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.ICustodiosDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/custodios")
public class CustodiosControlador {

	private final ICustodiosUseCase custodiosUseCase;
	private final ICustodiosDtoMapper mapper;

	public CustodiosControlador(ICustodiosUseCase custodiosUseCase, ICustodiosDtoMapper mapper) {
		this.custodiosUseCase = custodiosUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<?> crear(@Valid @RequestBody CustodiosRequestDTO request) {


		CustodiosResponseDTO creado = mapper.toResponseDto(custodiosUseCase.crear(mapper.toDomain(request)));

		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}

	@GetMapping
	public List<CustodiosResponseDTO> listar() {
		return custodiosUseCase.listar().stream().map(mapper::toResponseDto).toList();
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @Valid @RequestBody CustodiosRequestDTO request) {

		Custodios actualizado = custodiosUseCase.actualizar(id, mapper.toDomain(request));
		return ResponseEntity.ok(mapper.toResponseDto(actualizado));
	}

	@PutMapping("/estado/{id}")
	public ResponseEntity<CustodiosResponseDTO> actualizarEstado(@PathVariable int id,
			@RequestBody java.util.Map<String, Boolean> body) {
		boolean estado = Boolean.TRUE.equals(body.get("estado"));
		Custodios actualizado = custodiosUseCase.actualizarEstado(id, estado);
		return ResponseEntity.ok(mapper.toResponseDto(actualizado));
	}

	@GetMapping("/{id}")
	public ResponseEntity<CustodiosResponseDTO> obtenerPorId(@PathVariable int id) {
		Custodios c = custodiosUseCase.obtenerPorId(id);
		return ResponseEntity.ok(mapper.toResponseDto(c));
	}

	@GetMapping("/existe-cedula")
	public ResponseEntity<Boolean> existeCedula(@RequestParam String cedula,
			@RequestParam(required = false) Integer id) {
		boolean existe = (id == null) ? custodiosUseCase.existeCedula(cedula)
				: custodiosUseCase.existeCedulaParaOtro(cedula, id);

		return ResponseEntity.ok(existe);
	}

	@GetMapping("/existe-correo")
	public ResponseEntity<Boolean> existeCorreo(@RequestParam String correo,
			@RequestParam(required = false) Integer id) {
		boolean existe = (id == null) ? custodiosUseCase.existeCorreo(correo)
				: custodiosUseCase.existeCorreoParaOtro(correo, id);

		return ResponseEntity.ok(existe);
	}
}
