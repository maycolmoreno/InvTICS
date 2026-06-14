package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos.RegistrarRecepcionActivoCommand;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRegistrarRecepcionActivoUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoRecepcionLote;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoItemInventario;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoMovimientoInventario;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecepcionNoPermitidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.servicios.inventario.RecalcularEstadosService;
import com.uisrael.gestionactivosapi.dominio.servicios.inventario.RecepcionGuards;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.BodegaJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CategoriaEquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MarcasJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MovimientoInventarioJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraDetalleJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RecepcionLoteJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IBodegaJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICategoriaEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMarcasJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMovimientoInventarioJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IOrdenCompraDetalleJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IOrdenCompraJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRecepcionLoteJpaRepositorio;

@Service
@Transactional
public class RegistrarRecepcionActivoUseCaseImpl implements IRegistrarRecepcionActivoUseCase {

    private final IOrdenCompraJpaRepositorio ordenCompraRepo;
    private final IOrdenCompraDetalleJpaRepositorio detalleRepo;
    private final IBodegaJpaRepositorio bodegaRepo;
    private final IMarcasJpaRepositorio marcaRepo;
    private final ICategoriaEquiposJpaRepositorio categoriaRepo;
    private final IEquiposJpaRepositorio equiposRepo;
    private final IMovimientoInventarioJpaRepositorio movimientoRepo;
    private final IRecepcionLoteJpaRepositorio recepcionLoteRepo;
    private final RecepcionGuards guards;
    private final RecalcularEstadosService recalcularEstadosService;

    public RegistrarRecepcionActivoUseCaseImpl(IOrdenCompraJpaRepositorio ordenCompraRepo,
                                               IOrdenCompraDetalleJpaRepositorio detalleRepo,
                                               IBodegaJpaRepositorio bodegaRepo,
                                               IMarcasJpaRepositorio marcaRepo,
                                               ICategoriaEquiposJpaRepositorio categoriaRepo,
                                               IEquiposJpaRepositorio equiposRepo,
                                               IMovimientoInventarioJpaRepositorio movimientoRepo,
                                               IRecepcionLoteJpaRepositorio recepcionLoteRepo) {
        this.ordenCompraRepo = ordenCompraRepo;
        this.detalleRepo = detalleRepo;
        this.bodegaRepo = bodegaRepo;
        this.marcaRepo = marcaRepo;
        this.categoriaRepo = categoriaRepo;
        this.equiposRepo = equiposRepo;
        this.movimientoRepo = movimientoRepo;
        this.recepcionLoteRepo = recepcionLoteRepo;
        this.guards = new RecepcionGuards();
        this.recalcularEstadosService = new RecalcularEstadosService();
    }

    @Override
    public RecepcionLoteJpa ejecutar(RegistrarRecepcionActivoCommand command) {

        // 1. Cargar y validar OC
        OrdenCompraJpa oc = ordenCompraRepo.findById(command.getIdOrdenCompra())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Orden de compra no encontrada: " + command.getIdOrdenCompra()));
        guards.validarOrdenRecibible(oc.getEstado());

        // 2. Cargar y validar detalle
        OrdenCompraDetalleJpa detalle = detalleRepo.findById(command.getIdOrdenCompraDetalle())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Detalle de orden no encontrado: " + command.getIdOrdenCompraDetalle()));

        if (detalle.getTipoItem() != TipoItemInventario.ACTIVO) {
            throw new RecepcionNoPermitidaException(
                    "Solo se permite recepcion de items tipo ACTIVO en este flujo. Tipo recibido: "
                            + detalle.getTipoItem());
        }
        guards.validarDetalleRecibible(detalle.getEstado());
        // Cada activo se recibe de a 1
        guards.validarCantidad(1, detalle.getCantidadSolicitada(), detalle.getCantidadRecibida());

        // 3. Validar serial único
        if (equiposRepo.existsBySerialIgnoreCase(command.getSerial())) {
            throw new RecepcionNoPermitidaException(
                    "Ya existe un equipo con el serial: " + command.getSerial());
        }

        // 4. Cargar referencias
        BodegaJpa bodega = bodegaRepo.findById(command.getIdBodegaDestino())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Bodega no encontrada: " + command.getIdBodegaDestino()));
        MarcasJpa marca = marcaRepo.findById(command.getIdMarca())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Marca no encontrada: " + command.getIdMarca()));
        CategoriaEquiposJpa categoria = categoriaRepo.findById(command.getIdCategoria())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoría no encontrada: " + command.getIdCategoria()));

        LocalDateTime ahora = LocalDateTime.now();

        // 5. Crear RecepcionLote en estado APLICADO (cantidad = 1 por activo individual)
        RecepcionLoteJpa lote = new RecepcionLoteJpa();
        lote.setUuid(UUID.randomUUID().toString());
        lote.setOrdenCompra(oc);
        lote.setOrdenCompraDetalle(detalle);
        lote.setFechaRecepcion(ahora);
        lote.setCantidadRecibida(1);
        lote.setTipoItem(TipoItemInventario.ACTIVO);
        lote.setEstado(EstadoRecepcionLote.APLICADO);
        lote.setBodegaDestino(bodega);
        lote.setObservacion(command.getObservacion());
        lote.setRecepcionadoPor(command.getRecepcionadoPor());
        lote.setRecepcionadoEn(ahora);
        RecepcionLoteJpa loteGuardado = recepcionLoteRepo.save(lote);

        // 6. Crear equipo en bodega
        EquiposJpa equipo = new EquiposJpa();
        equipo.setModelo(command.getModelo());
        equipo.setSerial(command.getSerial());
        equipo.setCondicionAlRecibir(command.getCondicionAlRecibir());
        equipo.setEstadoEquipo("NUEVO");
        equipo.setEstadoInventario("EN_BODEGA");
        equipo.setEstado(true);
        equipo.setBodegaActual(bodega);
        equipo.setOrdenCompra(oc);
        equipo.setDetalleOc(detalle);
        equipo.setRecepcionLote(loteGuardado);
        equipo.setFkMarcas(marca);
        equipo.setFkCategoria(categoria);
        equipo.setProcesador(command.getProcesador());
        equipo.setMemoriaRamGb(command.getMemoriaRamGb());
        equipo.setCapacidadAlmacenamientoGb(command.getCapacidadAlmacenamientoGb());
        equipo.setLicenciaWindowsActivada(command.getLicenciaWindowsActivada());
        equipo.setMac(command.getMac());
        equipo.setFechaCompra(command.getFechaCompra());
        equipo.setFechaGarantia(command.getFechaGarantia());
        equipo.setPrecioCompra(command.getPrecioCompra());
        equipo.setEtiquetado(command.getEtiquetado());
        EquiposJpa equipoGuardado = equiposRepo.save(equipo);

        // 7. Registrar movimiento de inventario
        MovimientoInventarioJpa movimiento = new MovimientoInventarioJpa();
        movimiento.setTipoMovimiento(TipoMovimientoInventario.INGRESO_ACTIVO);
        movimiento.setFechaMovimiento(ahora);
        movimiento.setEquipo(equipoGuardado);
        movimiento.setCantidad(1);
        movimiento.setBodegaDestino(bodega);
        movimiento.setOrdenCompra(oc);
        movimiento.setRecepcionLote(loteGuardado);
        movimiento.setEstadoNuevo("EN_BODEGA");
        movimiento.setObservacion(command.getObservacion());
        movimientoRepo.save(movimiento);

        // 8. Actualizar cantidadRecibida del detalle
        int recibidaAcumulada = detalle.getCantidadRecibida() == null ? 0 : detalle.getCantidadRecibida();
        detalle.setCantidadRecibida(recibidaAcumulada + 1);
        detalleRepo.save(detalle);

        // 9. Recalcular estados
        List<OrdenCompraDetalleJpa> todosDetalles =
                detalleRepo.findByOrdenCompra_IdOrdenCompra(oc.getIdOrdenCompra());
        recalcularEstadosService.recalcularOrden(oc, todosDetalles);
        ordenCompraRepo.save(oc);

        return loteGuardado;
    }
}
