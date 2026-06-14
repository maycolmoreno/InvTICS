package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IOrdenCompraDetalleJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IOrdenCompraJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRecepcionLoteJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RegistrarRecepcionActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RegistrarRecepcionStockRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.OrdenCompraDetalleResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.RecepcionLoteResponseDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ordenes-compra")
public class RecepcionesControlador {

    private final IRegistrarRecepcionStockUseCase registrarStock;
    private final IRegistrarRecepcionActivoUseCase registrarActivo;
    private final IRecepcionLoteJpaRepositorio recepcionLoteRepo;
    private final IOrdenCompraJpaRepositorio ordenRepo;
    private final IOrdenCompraDetalleJpaRepositorio detalleRepo;

    public RecepcionesControlador(IRegistrarRecepcionStockUseCase registrarStock,
                                  IRegistrarRecepcionActivoUseCase registrarActivo,
                                  IRecepcionLoteJpaRepositorio recepcionLoteRepo,
                                  IOrdenCompraJpaRepositorio ordenRepo,
                                  IOrdenCompraDetalleJpaRepositorio detalleRepo) {
        this.registrarStock = registrarStock;
        this.registrarActivo = registrarActivo;
        this.recepcionLoteRepo = recepcionLoteRepo;
        this.ordenRepo = ordenRepo;
        this.detalleRepo = detalleRepo;
    }

    @GetMapping("/{idOC}")
    @Transactional(readOnly = true)
    public ResponseEntity<OrdenCompraResponseDTO> obtenerOrden(@PathVariable Integer idOC) {
        var orden = ordenRepo.findById(idOC)
                .orElseThrow(() -> new RecursoNoEncontradoException("OrdenCompra", idOC));

        OrdenCompraResponseDTO dto = new OrdenCompraResponseDTO();
        dto.setIdOrdenCompra(orden.getIdOrdenCompra());
        dto.setNumeroOc(orden.getNumeroOc());
        dto.setProveedor(orden.getProveedor());
        dto.setFechaEmision(orden.getFechaEmision());
        dto.setFechaRecepcion(orden.getFechaRecepcion());
        dto.setEstado(orden.getEstado());
        dto.setObservacion(orden.getObservacion());
        if (orden.getBodegaDestino() != null) {
            dto.setBodegaDestinoId(orden.getBodegaDestino().getIdBodega());
            dto.setBodegaDestinoNombre(orden.getBodegaDestino().getNombre());
        }
        dto.setDetalles(detalleRepo.findByOrdenCompra_IdOrdenCompra(idOC).stream()
                .map(d -> {
                    OrdenCompraDetalleResponseDTO detDto = new OrdenCompraDetalleResponseDTO();
                    detDto.setIdOrdenCompraDetalle(d.getIdOrdenCompraDetalle());
                    detDto.setTipoItem(d.getTipoItem());
                    detDto.setEstado(d.getEstado() != null ? d.getEstado().name() : null);
                    detDto.setDescripcion(d.getDescripcion());
                    detDto.setCantidadSolicitada(d.getCantidadSolicitada());
                    detDto.setCantidadRecibida(d.getCantidadRecibida());
                    if (d.getCategoria() != null) {
                        detDto.setCategoriaId(d.getCategoria().getIdCategoria());
                        detDto.setCategoriaNombre(d.getCategoria().getNombre());
                    }
                    if (d.getMarca() != null) {
                        detDto.setMarcaId(d.getMarca().getIdMarca());
                        detDto.setMarcaNombre(d.getMarca().getNombre());
                    }
                    if (d.getConsumible() != null) {
                        detDto.setConsumibleId(d.getConsumible().getIdConsumible());
                        detDto.setConsumibleNombre(d.getConsumible().getNombre());
                    }
                    return detDto;
                })
                .toList());
        return ResponseEntity.ok(dto);
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
