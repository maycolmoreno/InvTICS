package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IActualizarActivoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IBuscarActivoPorIdUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Activo;

@RestController
@RequestMapping("/api/activos")
@CrossOrigin(origins = "*")
public class ActivoController {

	private final IBuscarActivoPorIdUseCase buscarActivoPorIdUseCase;
	private final IActualizarActivoUseCase actualizarActivoUseCase;

	public ActivoController(IBuscarActivoPorIdUseCase buscarActivoPorIdUseCase,
			IActualizarActivoUseCase actualizarActivoUseCase) {
		this.buscarActivoPorIdUseCase = buscarActivoPorIdUseCase;
		this.actualizarActivoUseCase = actualizarActivoUseCase;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Activo> buscarPorId(@PathVariable int id) {
		Optional<Activo> activo = buscarActivoPorIdUseCase.ejecutar(id);
		return activo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> actualizar(@PathVariable int id, @RequestBody Activo activo) {
		activo.setIdActivo(id);
		actualizarActivoUseCase.ejecutar(activo);
		return ResponseEntity.ok().build();
	}
}
