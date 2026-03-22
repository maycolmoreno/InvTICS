package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IVisitaTecnicaUseCase;
import com.uisrael.gestionactivosapi.presentacion.dto.response.VisitaCustodioResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.VisitaEquipoResponseDTO;

@RestController
@RequestMapping("/api/visita")
public class VisitaTecnicaControlador {

    private final IVisitaTecnicaUseCase visitaUseCase;

    public VisitaTecnicaControlador(IVisitaTecnicaUseCase visitaUseCase) {
        this.visitaUseCase = visitaUseCase;
    }

    @GetMapping("/equipos")
    public ResponseEntity<List<VisitaEquipoResponseDTO>> obtenerEquipos(
            @RequestParam Long ubicacionId,
            @RequestParam(required = false) Long custodioId) {
        if (ubicacionId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(visitaUseCase.obtenerEquipos(ubicacionId, custodioId));
    }

    @GetMapping("/custodios")
    public ResponseEntity<List<VisitaCustodioResponseDTO>> obtenerCustodios(
            @RequestParam Long ubicacionId) {
        if (ubicacionId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(visitaUseCase.obtenerCustodios(ubicacionId));
    }
}
