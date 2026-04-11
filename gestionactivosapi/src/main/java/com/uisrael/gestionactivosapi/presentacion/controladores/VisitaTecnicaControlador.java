package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IVisitaTecnicaUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.CustodioVisita;
import com.uisrael.gestionactivosapi.dominio.entidades.EquipoVisita;
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
        List<EquipoVisita> equipos = visitaUseCase.obtenerEquipos(ubicacionId, custodioId);
        List<VisitaEquipoResponseDTO> resultado = equipos.stream().map(e -> {
            VisitaEquipoResponseDTO dto = new VisitaEquipoResponseDTO();
            dto.setIdEquipo(e.getIdEquipo());
            dto.setSerial(e.getSerial());
            dto.setMarca(e.getMarca());
            dto.setModelo(e.getModelo());
            dto.setCodigoSap(e.getCodigoSap());
            dto.setCustodioNombre(e.getCustodioNombre());
            dto.setCustodioArea(e.getCustodioArea());
            dto.setUbicacionNombre(e.getUbicacionNombre());
            dto.setFechaUltimoMantenimiento(e.getFechaUltimoMantenimientoLocal());
            dto.setDiasSinMantenimiento(e.getDiasSinMantenimiento());
            dto.setEstadoMantenimiento(e.getEstadoMantenimiento().name());
            return dto;
        }).toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/custodios")
    public ResponseEntity<List<VisitaCustodioResponseDTO>> obtenerCustodios(
            @RequestParam Long ubicacionId) {
        if (ubicacionId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<CustodioVisita> custodios = visitaUseCase.obtenerCustodios(ubicacionId);
        List<VisitaCustodioResponseDTO> resultado = custodios.stream().map(c -> {
            VisitaCustodioResponseDTO dto = new VisitaCustodioResponseDTO();
            dto.setIdCustodio(c.idCustodio());
            dto.setNombre(c.nombre());
            dto.setArea(c.area());
            return dto;
        }).toList();
        return ResponseEntity.ok(resultado);
    }
}
