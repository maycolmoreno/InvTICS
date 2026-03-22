package com.uisrael.gestionactivosapi.presentacion.controladores;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerHistorialEquipoUseCase;
import com.uisrael.gestionactivosapi.dominio.dto.HistorialCompletoDTO;

@RestController
@RequestMapping("/api/historial")
public class HistorialEquipoControlador {

    private final IObtenerHistorialEquipoUseCase historialUseCase;

    public HistorialEquipoControlador(IObtenerHistorialEquipoUseCase historialUseCase) {
        this.historialUseCase = historialUseCase;
    }

    @GetMapping("/{equipoId}")
    public HistorialCompletoDTO obtener(@PathVariable Long equipoId) {
        return historialUseCase.obtener(equipoId);
    }
}
