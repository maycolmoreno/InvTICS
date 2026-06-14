package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos.RegistrarRecepcionStockCommand;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRegistrarRecepcionStockUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoRecepcionLote;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoItemInventario;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoMovimientoInventario;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecepcionNoPermitidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.servicios.inventario.RecalcularEstadosService;
import com.uisrael.gestionactivosapi.dominio.servicios.inventario.RecepcionGuards;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.BodegaJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ConsumibleJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MovimientoInventarioJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraDetalleJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RecepcionLoteJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.StockConsumibleBodegaJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IBodegaJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMovimientoInventarioJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IOrdenCompraDetalleJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IOrdenCompraJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRecepcionLoteJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IStockConsumibleBodegaJpaRepositorio;

@Service
@Transactional
public class RegistrarRecepcionStockUseCaseImpl implements IRegistrarRecepcionStockUseCase {

    private final IOrdenCompraJpaRepositorio ordenCompraRepo;
    private final IOrdenCompraDetalleJpaRepositorio detalleRepo;
    private final IBodegaJpaRepositorio bodegaRepo;
    private final IStockConsumibleBodegaJpaRepositorio stockRepo;
    private final IMovimientoInventarioJpaRepositorio movimientoRepo;
    private final IRecepcionLoteJpaRepositorio recepcionLoteRepo;
    private final RecepcionGuards guards;
    private final RecalcularEstadosService recalcularEstadosService;

    public RegistrarRecepcionStockUseCaseImpl(IOrdenCompraJpaRepositorio ordenCompraRepo,
                                              IOrdenCompraDetalleJpaRepositorio detalleRepo,
                                              IBodegaJpaRepositorio bodegaRepo,
                                              IStockConsumibleBodegaJpaRepositorio stockRepo,
                                              IMovimientoInventarioJpaRepositorio movimientoRepo,
                                              IRecepcionLoteJpaRepositorio recepcionLoteRepo) {
        this.ordenCompraRepo = ordenCompraRepo;
        this.detalleRepo = detalleRepo;
        this.bodegaRepo = bodegaRepo;
        this.stockRepo = stockRepo;
        this.movimientoRepo = movimientoRepo;
        this.recepcionLoteRepo = recepcionLoteRepo;
        this.guards = new RecepcionGuards();
        this.recalcularEstadosService = new RecalcularEstadosService();
    }

    @Override
    public RecepcionLoteJpa ejecutar(RegistrarRecepcionStockCommand command) {

        // 1. Cargar y validar OC
        OrdenCompraJpa oc = ordenCompraRepo.findById(command.getIdOrdenCompra())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Orden de compra no encontrada: " + command.getIdOrdenCompra()));
        guards.validarOrdenRecibible(oc.getEstado());

        // 2. Cargar y validar detalle
        OrdenCompraDetalleJpa detalle = detalleRepo.findById(command.getIdOrdenCompraDetalle())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Detalle de orden no encontrado: " + command.getIdOrdenCompraDetalle()));

        if (detalle.getTipoItem() != TipoItemInventario.STOCK) {
            throw new RecepcionNoPermitidaException(
                    "Solo se permite recepcion de items tipo STOCK. Tipo recibido: " + detalle.getTipoItem());
        }
        guards.validarDetalleRecibible(detalle.getEstado());
        guards.validarCantidad(command.getCantidad(), detalle.getCantidadSolicitada(), detalle.getCantidadRecibida());

        // 3. Cargar bodega destino
        BodegaJpa bodega = bodegaRepo.findById(command.getIdBodegaDestino())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Bodega no encontrada: " + command.getIdBodegaDestino()));

        // 4. Obtener consumible asociado al detalle
        ConsumibleJpa consumible = detalle.getConsumible();
        if (consumible == null) {
            throw new RecepcionNoPermitidaException(
                    "El detalle STOCK id=" + command.getIdOrdenCompraDetalle() + " no tiene consumible asociado");
        }

        LocalDateTime ahora = LocalDateTime.now();

        // 5. Crear RecepcionLote en estado APLICADO
        RecepcionLoteJpa lote = new RecepcionLoteJpa();
        lote.setUuid(UUID.randomUUID().toString());
        lote.setOrdenCompra(oc);
        lote.setOrdenCompraDetalle(detalle);
        lote.setFechaRecepcion(ahora);
        lote.setCantidadRecibida(command.getCantidad());
        lote.setTipoItem(TipoItemInventario.STOCK);
        lote.setEstado(EstadoRecepcionLote.APLICADO);
        lote.setBodegaDestino(bodega);
        lote.setObservacion(command.getObservacion());
        lote.setRecepcionadoPor(command.getRecepcionadoPor());
        lote.setRecepcionadoEn(ahora);
        RecepcionLoteJpa loteGuardado = recepcionLoteRepo.save(lote);

        // 6. Incrementar stock en bodega (crear registro si no existe)
        StockConsumibleBodegaJpa stock = stockRepo
                .findByBodega_IdBodegaAndConsumible_IdConsumible(
                        bodega.getIdBodega(), consumible.getIdConsumible())
                .orElseGet(() -> {
                    StockConsumibleBodegaJpa nuevo = new StockConsumibleBodegaJpa();
                    nuevo.setBodega(bodega);
                    nuevo.setConsumible(consumible);
                    nuevo.setCantidad(0);
                    return nuevo;
                });
        stock.setCantidad(stock.getCantidad() + command.getCantidad());
        stockRepo.save(stock);

        // 7. Registrar movimiento de inventario
        MovimientoInventarioJpa movimiento = new MovimientoInventarioJpa();
        movimiento.setTipoMovimiento(TipoMovimientoInventario.INGRESO_CONSUMIBLE);
        movimiento.setFechaMovimiento(ahora);
        movimiento.setConsumible(consumible);
        movimiento.setCantidad(command.getCantidad());
        movimiento.setBodegaDestino(bodega);
        movimiento.setOrdenCompra(oc);
        movimiento.setRecepcionLote(loteGuardado);
        movimiento.setEstadoNuevo("EN_BODEGA");
        movimiento.setObservacion(command.getObservacion());
        movimientoRepo.save(movimiento);

        // 8. Actualizar cantidadRecibida del detalle y persistir
        int recibidaAcumulada = detalle.getCantidadRecibida() == null ? 0 : detalle.getCantidadRecibida();
        detalle.setCantidadRecibida(recibidaAcumulada + command.getCantidad());
        detalleRepo.save(detalle);

        // 9. Recalcular estados usando todos los detalles de la OC
        List<OrdenCompraDetalleJpa> todosDetalles =
                detalleRepo.findByOrdenCompra_IdOrdenCompra(oc.getIdOrdenCompra());
        recalcularEstadosService.recalcularOrden(oc, todosDetalles);
        ordenCompraRepo.save(oc);

        return loteGuardado;
    }
}
