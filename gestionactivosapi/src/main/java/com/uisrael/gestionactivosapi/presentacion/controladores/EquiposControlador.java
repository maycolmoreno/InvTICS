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

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IEquiposUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;
import com.uisrael.gestionactivosapi.dominio.modelo.Pagina;
import com.uisrael.gestionactivosapi.presentacion.dto.request.EquiposRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.EquiposResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.PaginaResponse;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.IEquiposDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/equipos")
public class EquiposControlador {

	private final IEquiposUseCase equiposUseCase;
	private final IEquiposDtoMapper mapper;

	public EquiposControlador(IEquiposUseCase equiposUseCase, IEquiposDtoMapper mapper) {
		this.equiposUseCase = equiposUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<?> crear(@Valid @RequestBody EquiposRequestDTO request) {

		EquiposResponseDTO creado = mapper.toResponseDto(equiposUseCase.crear(mapper.toDomain(request)));

		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}

	@GetMapping
	public List<EquiposResponseDTO> listar() {
		return equiposUseCase.listar().stream().map(mapper::toResponseDto).toList();
	}

	@GetMapping("/paginado")
	public PaginaResponse<EquiposResponseDTO> listarPaginado(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		Pagina<Equipos> pagina = equiposUseCase.listarPaginado(page, size);
		List<EquiposResponseDTO> contenido = pagina.contenido().stream()
				.map(mapper::toResponseDto).toList();
		return new PaginaResponse<>(contenido, pagina.paginaActual(),
				pagina.tamanioPagina(), pagina.totalElementos(), pagina.totalPaginas());
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @Valid @RequestBody EquiposRequestDTO request) {

		Equipos actualizado = equiposUseCase.actualizar(id, mapper.toDomain(request));
		return ResponseEntity.ok(mapper.toResponseDto(actualizado));
	}

	@PutMapping("/estado/{id}")
	public ResponseEntity<?> actualizarEstado(@PathVariable int id, @RequestParam boolean estado) {
		equiposUseCase.actualizarEstado(id, estado);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<EquiposResponseDTO> obtenerPorId(@PathVariable int id) {
		Equipos equipo = equiposUseCase.obtenerPorId(id);
		return ResponseEntity.ok(mapper.toResponseDto(equipo));
	}

	@GetMapping("/existe-codigo")
	public ResponseEntity<Boolean> existeCodigo(@RequestParam String codigo,
			@RequestParam(required = false) Integer id) {
		boolean existe = (id == null) ? equiposUseCase.existeCodigo(codigo)
				: equiposUseCase.existeCodigoParaOtro(codigo, id);

		return ResponseEntity.ok(existe);
	}

	@GetMapping("/existe-serial")
	public ResponseEntity<Boolean> existeSerial(@RequestParam String serial,
			@RequestParam(required = false) Integer id) {
		boolean existe = (id == null) ? equiposUseCase.existeSerial(serial)
				: equiposUseCase.existeSerialParaOtro(serial, id);

		return ResponseEntity.ok(existe);
	}

	@GetMapping("/existe-mac")
	public ResponseEntity<Boolean> existeMAC(@RequestParam String mac, @RequestParam(required = false) Integer id) {
		boolean existe = (id == null) ? equiposUseCase.existeMAC(mac) : equiposUseCase.existeMACParaOtro(mac, id);

		return ResponseEntity.ok(existe);
	}

}

