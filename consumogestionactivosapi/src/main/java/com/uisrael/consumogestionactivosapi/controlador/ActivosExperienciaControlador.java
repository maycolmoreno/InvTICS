package com.uisrael.consumogestionactivosapi.controlador;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoManualResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.MovimientoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoManualServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/activos")
public class ActivosExperienciaControlador {

    private final IEquiposServicio equiposServicio;
    private final ICustodiasServicio custodiasServicio;
    private final IMantenimientoManualServicio mantenimientoManualServicio;
    private final IInventarioOperacionServicio inventarioOperacionServicio;

    @GetMapping("/equipos/{idEquipo}/expediente")
    public String expedienteActivo(@PathVariable Integer idEquipo, Model model) {
        EquiposResponseDTO equipo = equiposServicio.obtenerPorId(idEquipo);
        List<CustodiasResponseDTO> custodias = safeList(custodiasServicio.listarCustodias()).stream()
                .filter(c -> c.getFkEquipo() != null && c.getFkEquipo().getIdEquipo() == idEquipo)
                .toList();
        CustodiasResponseDTO custodiaActual = custodias.stream()
                .filter(CustodiasResponseDTO::isEstado)
                .findFirst()
                .orElse(null);
        List<MantenimientoManualResponseDTO> mantenimientos = safeList(mantenimientoManualServicio.obtenerHistorial(idEquipo));
        List<MovimientoInventarioResponseDTO> movimientos = safeList(inventarioOperacionServicio.listarMovimientosRecientes()).stream()
                .filter(m -> equipoCoincide(m, equipo))
                .limit(10)
                .toList();

        model.addAttribute("equipo", equipo);
        model.addAttribute("custodiaActual", custodiaActual);
        model.addAttribute("historialCustodias", custodias);
        model.addAttribute("mantenimientos", mantenimientos);
        model.addAttribute("movimientos", movimientos);
        return "Activos/expedienteActivo";
    }

    private boolean equipoCoincide(MovimientoInventarioResponseDTO movimiento, EquiposResponseDTO equipo) {
        if (movimiento.getEquipoId() != null && movimiento.getEquipoId().equals(equipo.getIdEquipo())) {
            return true;
        }
        return movimiento.getEquipoCodigo() != null && equipo.getCodigoSap() != null
                && movimiento.getEquipoCodigo().equalsIgnoreCase(equipo.getCodigoSap());
    }

    private <T> List<T> safeList(List<T> value) {
        return value == null ? new ArrayList<>() : value;
    }
}
