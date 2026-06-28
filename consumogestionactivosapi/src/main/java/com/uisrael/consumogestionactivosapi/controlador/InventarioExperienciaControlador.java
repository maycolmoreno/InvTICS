package com.uisrael.consumogestionactivosapi.controlador;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AsignacionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.BajaActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.OrdenCompraRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RegistrarRecepcionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RegistrarRecepcionStockRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventario")
public class InventarioExperienciaControlador {

    private final IInventarioOperacionServicio inventarioOperacionServicio;
    private final IMarcasServicio marcasServicio;
    private final ICategoriaEquiposServicio categoriaEquiposServicio;
    private final ICustodiosServicio custodiosServicio;

    @GetMapping("/compras")
    public String compras(Model model) {
        model.addAttribute("ordenesCompra", safeList(inventarioOperacionServicio.listarOrdenesCompra()));
        model.addAttribute("bodegas", bodegasActivas());
        model.addAttribute("consumibles", safeList(inventarioOperacionServicio.listarConsumibles()).stream()
                .filter(ConsumibleResponseDTO::isEstado)
                .toList());
        model.addAttribute("ordenCompraRequest", new OrdenCompraRequestDTO());
        return "Inventario/compras";
    }

    @GetMapping("/ordenes-compra/{id}/gestionar")
    public String gestionarOC(@PathVariable Integer id, Model model) {
        var oc = inventarioOperacionServicio.obtenerOrdenCompra(id);
        model.addAttribute("oc", oc);
        model.addAttribute("recepciones", safeList(inventarioOperacionServicio.listarRecepciones(id)));
        model.addAttribute("bodegas", bodegasActivas());
        model.addAttribute("marcas", marcasServicio.listarMarca());
        model.addAttribute("categorias", categoriaEquiposServicio.listarCategoriaEquipo());
        model.addAttribute("recepcionStockRequest", new RegistrarRecepcionStockRequestDTO());
        model.addAttribute("recepcionActivoRequest", new RegistrarRecepcionActivoRequestDTO());
        return "Inventario/gestionarOC";
    }

    @GetMapping("/recepcion")
    public String recepcion() {
        return "redirect:/inventario/compras";
    }

    @GetMapping("/stock")
    public String stock(Model model) {
        var bodegas = bodegasActivas();
        Integer bodegaSeleccionadaId = bodegas.isEmpty() ? null : bodegas.get(0).getIdBodega();
        model.addAttribute("bodegas", bodegas);
        model.addAttribute("stock", bodegaSeleccionadaId == null
                ? List.of()
                : safeList(inventarioOperacionServicio.listarStockPorBodega(bodegaSeleccionadaId)));
        model.addAttribute("bodegaSeleccionadaId", bodegaSeleccionadaId);
        model.addAttribute("custodios", safeList(custodiosServicio.listarCustodios()));
        model.addAttribute("asignacionConsumibleRequest", new AsignacionConsumibleRequestDTO());
        model.addAttribute("trasladoConsumibleRequest", new TrasladoConsumibleRequestDTO());
        return "Inventario/stock";
    }

    @GetMapping("/traslados")
    public String traslados(Model model) {
        var bodegas = bodegasActivas();
        Integer bodegaSeleccionadaId = bodegas.isEmpty() ? null : bodegas.get(0).getIdBodega();
        model.addAttribute("bodegas", bodegas);
        model.addAttribute("activosEnBodega", safeList(inventarioOperacionServicio.listarActivosEnBodega()));
        model.addAttribute("activosEnTransito", safeList(inventarioOperacionServicio.listarActivosEnTransito()));
        model.addAttribute("stock", bodegaSeleccionadaId == null
                ? List.of()
                : safeList(inventarioOperacionServicio.listarStockPorBodega(bodegaSeleccionadaId)));
        model.addAttribute("trasladoActivoRequest", new TrasladoActivoRequestDTO());
        model.addAttribute("trasladoConsumibleRequest", new TrasladoConsumibleRequestDTO());
        return "Inventario/traslados";
    }

    @GetMapping("/bajas")
    public String bajas(Model model) {
        model.addAttribute("activosEnBodega", safeList(inventarioOperacionServicio.listarActivosEnBodega()));
        model.addAttribute("bajaActivoRequest", new BajaActivoRequestDTO());
        model.addAttribute("movimientos", safeList(inventarioOperacionServicio.listarMovimientosRecientes()).stream()
                .filter(m -> "BAJA".equals(m.getTipoMovimiento()))
                .limit(8)
                .toList());
        return "Inventario/bajas";
    }

    private <T> List<T> safeList(List<T> value) {
        return value == null ? new ArrayList<>() : value;
    }

    private List<BodegaResponseDTO> bodegasActivas() {
        return safeList(inventarioOperacionServicio.listarBodegas()).stream()
                .filter(BodegaResponseDTO::isEstado)
                .toList();
    }
}
