package com.uisrael.consumogestionactivosapi.controlador;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.MovimientoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/custodias")
public class CustodiasExpedienteControlador {

    private final ICustodiosServicio custodiosServicio;
    private final ICustodiasServicio custodiasServicio;
    private final IInventarioOperacionServicio inventarioOperacionServicio;

    @GetMapping("/expediente/{idCustodio}")
    public String expedienteCustodio(@PathVariable Integer idCustodio, Model model) {
        CustodiosResponseDTO custodio = custodiosServicio.obtenerPorId(idCustodio);
        List<CustodiasResponseDTO> custodias = safeList(custodiasServicio.listarCustodias()).stream()
                .filter(c -> c.getIdCustodio() == idCustodio
                        || (c.getFkCustodio() != null && c.getFkCustodio().getIdCustodio() == idCustodio))
                .toList();
        List<CustodiasResponseDTO> custodiasActivas = custodias.stream()
                .filter(CustodiasResponseDTO::isEstado)
                .toList();
        List<MovimientoInventarioResponseDTO> movimientos = safeList(inventarioOperacionServicio.listarMovimientosRecientes()).stream()
                .filter(m -> m.getCustodioId() != null && m.getCustodioId().equals(idCustodio))
                .limit(12)
                .toList();
        long consumiblesEntregados = movimientos.stream()
                .filter(m -> m.getConsumibleId() != null)
                .map(MovimientoInventarioResponseDTO::getConsumibleId)
                .distinct()
                .count();

        model.addAttribute("custodio", custodio);
        model.addAttribute("custodias", custodias);
        model.addAttribute("custodiasActivas", custodiasActivas);
        model.addAttribute("movimientos", movimientos);
        model.addAttribute("consumiblesEntregados", consumiblesEntregados);
        model.addAttribute("bodegas", safeCall(inventarioOperacionServicio::listarBodegas).stream()
                .filter(b -> b != null && b.isEstado())
                .toList());
        return "Custodias/expedienteCustodio";
    }

    private <T> List<T> safeList(List<T> value) {
        return value == null ? new ArrayList<>() : value;
    }

    private <T> List<T> safeCall(java.util.function.Supplier<List<T>> supplier) {
        try {
            List<T> result = supplier.get();
            return result == null ? new ArrayList<>() : result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
