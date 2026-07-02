package com.uisrael.gestionactivosapi.presentacion.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos.ActividadRealizadaComando;
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

    // RETIRADO (Fase C2): este subsistema de OT paralelo cerraba mantenimientos sin
    // resultado tecnico, saltandose la RN-004. El flujo vigente es Mantenimiento Manual
    // (/api/mantenimiento/**). Los endpoints de escritura quedan deshabilitados para
    // impedir cierres inconsistentes por llamadas directas. La lectura se mantiene
    // temporalmente hasta la eliminacion definitiva del vertical.
    private static final String MENSAJE_RETIRADO =
            "Endpoint retirado. Use el flujo de Mantenimiento Manual (/api/mantenimiento).";

    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody OrdenCrearRequestDTO request) {
        return ResponseEntity.status(HttpStatus.GONE).body(MENSAJE_RETIRADO);
    }

    @GetMapping("/{id}")
    public OrdenTrabajoResponseDTO obtener(@PathVariable Integer id) {
        return obtenerUseCase.obtener(id);
    }

    @PostMapping("/{id}/guardar")
    public ResponseEntity<String> guardar(@PathVariable Integer id, @RequestBody OrdenGuardarRequestDTO request) {
        return ResponseEntity.status(HttpStatus.GONE).body(MENSAJE_RETIRADO);
    }
}
