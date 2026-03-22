package com.uisrael.gestionactivosapi.presentacion.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICrearMantenimientosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IGuardarMantenimientoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerOrdenTrabajoUseCase;
import com.uisrael.gestionactivosapi.presentacion.dto.request.OrdenCrearRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.OrdenGuardarRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.OrdenCrearResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.OrdenTrabajoResponseDTO;

@RestController
@RequestMapping("/api/orden")
public class OrdenTrabajoControlador {

    private final ICrearMantenimientosUseCase crearUseCase;
    private final IGuardarMantenimientoUseCase guardarUseCase;
    private final IObtenerOrdenTrabajoUseCase obtenerUseCase;

    public OrdenTrabajoControlador(ICrearMantenimientosUseCase crearUseCase,
            IGuardarMantenimientoUseCase guardarUseCase,
            IObtenerOrdenTrabajoUseCase obtenerUseCase) {
        this.crearUseCase = crearUseCase;
        this.guardarUseCase = guardarUseCase;
        this.obtenerUseCase = obtenerUseCase;
    }

    @PostMapping("/crear")
    public ResponseEntity<OrdenCrearResponseDTO> crear(@RequestBody OrdenCrearRequestDTO request) {
        Integer id = crearUseCase.crear(
                request.getEquiposIds(),
                request.getTipo(),
                request.getPrioridad(),
                request.getIdUsuarioTecnico());
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrdenCrearResponseDTO(id));
    }

    @GetMapping("/{id}")
    public OrdenTrabajoResponseDTO obtener(@PathVariable Integer id) {
        return obtenerUseCase.obtener(id);
    }

    @PostMapping("/{id}/guardar")
    public ResponseEntity<Void> guardar(@PathVariable Integer id, @RequestBody OrdenGuardarRequestDTO request) {
        guardarUseCase.guardar(id, request.getActividades(), request.getObservaciones(),
                request.getEstadoGeneral(), request.getFirmaBase64());
        return ResponseEntity.ok().build();
    }
}
