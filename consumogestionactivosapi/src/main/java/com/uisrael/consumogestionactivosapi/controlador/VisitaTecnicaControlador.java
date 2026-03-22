package com.uisrael.consumogestionactivosapi.controlador;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionesResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.VisitaCustodioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.VisitaEquipoResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;
import com.uisrael.consumogestionactivosapi.service.IVisitaTecnicaServicio;

@Controller
public class VisitaTecnicaControlador {

    private final IUbicacionesServicio ubicacionesServicio;
    private final IVisitaTecnicaServicio visitaServicio;

    public VisitaTecnicaControlador(IUbicacionesServicio ubicacionesServicio,
            IVisitaTecnicaServicio visitaServicio) {
        this.ubicacionesServicio = ubicacionesServicio;
        this.visitaServicio = visitaServicio;
    }

    @GetMapping("/visita")
    public String vistaPrincipal(Model model) {
        List<UbicacionesResponseDTO> ubicaciones = ubicacionesServicio.listarUbicaciones();
        model.addAttribute("ubicaciones", ubicaciones);
        return "Visita/visita-tecnica";
    }

    @GetMapping("/api/visita/equipos")
    @ResponseBody
    public List<VisitaEquipoResponseDTO> obtenerEquipos(
            @RequestParam Long ubicacionId,
            @RequestParam(required = false) Long custodioId) {
        return visitaServicio.obtenerEquipos(ubicacionId, custodioId);
    }

    @GetMapping("/api/visita/custodios")
    @ResponseBody
    public List<VisitaCustodioResponseDTO> obtenerCustodios(
            @RequestParam Long ubicacionId) {
        return visitaServicio.obtenerCustodios(ubicacionId);
    }
}
