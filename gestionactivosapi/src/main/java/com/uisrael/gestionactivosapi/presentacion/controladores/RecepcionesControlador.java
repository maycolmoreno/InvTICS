package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos.RegistrarRecepcionActivoCommand;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos.RegistrarRecepcionStockCommand;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRegistrarRecepcionActivoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRegistrarRecepcionStockUseCase;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRecepcionLoteJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RegistrarRecepcionActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RegistrarRecepcionStockRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.RecepcionLoteResponseDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ordenes-compra")
public class RecepcionesControlador {

    private final IRegistrarRecepcionStockUseCase registrarStock;
    private final IRegistrarRecepcionActivoUseCase registrarActivo;
    private final IRecepcionLoteJpaRepositorio recepcionLoteRepo;

    public RecepcionesControlador(IRegistrarRecepcionStockUseCase registrarStock,
                                  IRegistrarRecepcionActivoUseCase registrarActivo,
                                  IRecepcionLoteJpaRepositorio recepcionLoteRepo) {
        this.registrarStock = registrarStock;
        this.registrarActivo = registrarActivo;
        this.recepcionLoteRepo = recepcionLoteRepo;
    }

    @PostMapping("/{idOC}/detalles/{idDetalle}/recepciones/stock")
    public ResponseEntity<RecepcionLoteResponseDTO> recibirStock(
            @PathVariable Integer idOC,
            @PathVariable Integer idDetalle,
            @Valid @RequestBody RegistrarRecepcionStockRequestDTO request) {

        RegistrarRecepcionStockCommand command = new RegistrarRecepcionStockCommand(
                idOC,
                idDetalle,
                request.getIdBodegaDestino(),
                request.getCantidad(),
                request.getRecepcionadoPor(),
                request.getObservacion());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RecepcionLoteResponseDTO.from(registrarStock.ejecutar(command)));
    }

    @PostMapping("/{idOC}/detalles/{idDetalle}/recepciones/activo")
    public ResponseEntity<RecepcionLoteResponseDTO> recibirActivo(
            @PathVariable Integer idOC,
            @PathVariable Integer idDetalle,
            @Valid @RequestBody RegistrarRecepcionActivoRequestDTO request) {

        RegistrarRecepcionActivoCommand command = new RegistrarRecepcionActivoCommand(
                idOC,
                idDetalle,
                request.getIdBodegaDestino(),
                request.getIdCategoria(),
                request.getIdMarca(),
                request.getModelo(),
                request.getSerial(),
                request.getCondicionAlRecibir(),
                request.getRecepcionadoPor(),
                request.getObservacion(),
                request.getProcesador(),
                request.getMemoriaRamGb(),
                request.getCapacidadAlmacenamientoGb(),
                request.getLicenciaWindowsActivada(),
                request.getMac(),
                request.getFechaCompra(),
                request.getFechaGarantia(),
                request.getPrecioCompra(),
                request.getEtiquetado());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RecepcionLoteResponseDTO.from(registrarActivo.ejecutar(command)));
    }

    @GetMapping("/{idOC}/recepciones")
    public List<RecepcionLoteResponseDTO> listarRecepciones(@PathVariable Integer idOC) {
        return recepcionLoteRepo.findByOrdenCompra_IdOrdenCompra(idOC)
                .stream()
                .map(RecepcionLoteResponseDTO::from)
                .toList();
    }
}
