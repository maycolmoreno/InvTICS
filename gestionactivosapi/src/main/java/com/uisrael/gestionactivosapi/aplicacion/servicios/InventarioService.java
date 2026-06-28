package com.uisrael.gestionactivosapi.aplicacion.servicios;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.dominio.entidades.EstadoInternoMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoInventarioActivo;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompra;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoItemInventario;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoMovimientoInventario;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.BodegaJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CategoriaEquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ConsumibleJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiasJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.DepartamentosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MarcasJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MovimientoInventarioJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraDetalleJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.StockConsumibleBodegaJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IBodegaJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICategoriaEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IConsumibleJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiasJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMarcasJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMovimientoInventarioJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IOrdenCompraDetalleJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IOrdenCompraJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IStockConsumibleBodegaJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.AsignacionActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.AsignacionLoteRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.AsignacionConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.BajaActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.BodegaRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.ConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.DevolucionActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.DevolucionConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.OrdenCompraDetalleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.OrdenCompraRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.TrasladoActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.TrasladoConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.AsignacionActivosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.ConsumibleResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.MovimientoInventarioResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.OrdenCompraDetalleResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.RecepcionLoteResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.StockConsumibleResponseDTO;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoRecepcionLote;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RecepcionLoteJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRecepcionLoteJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.AdoptarInventarioInicialRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.ConfirmarLlegadaActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.EnviarReparacionRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RegistrarRecepcionActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RegistrarRecepcionStockRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RetornarReparacionRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CargosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CategoriaEquiposResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CustodiasResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CustodiosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.DepartamentosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.EquiposResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MarcasResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.UbicacionesResponseDTO;
import java.util.UUID;

@Service
public class InventarioService {

    private final IBodegaJpaRepositorio bodegaRepo;
    private final IConsumibleJpaRepositorio consumibleRepo;
    private final IOrdenCompraJpaRepositorio ordenRepo;
    private final IOrdenCompraDetalleJpaRepositorio detalleRepo;
    private final IStockConsumibleBodegaJpaRepositorio stockRepo;
    private final IMovimientoInventarioJpaRepositorio movimientoRepo;
    private final IEquiposJpaRepositorio equiposRepo;
    private final ICustodiasJpaRepositorio custodiasRepo;
    private final ICategoriaEquiposJpaRepositorio categoriaRepo;
    private final IMarcasJpaRepositorio marcasRepo;
    private final ICustodiosJpaRepositorio custodiosRepo;
    private final IMantenimientosJpaRepositorio mantenimientosRepo;
    private final IRecepcionLoteJpaRepositorio recepcionLoteRepo;

    public InventarioService(IBodegaJpaRepositorio bodegaRepo,
            IConsumibleJpaRepositorio consumibleRepo,
            IOrdenCompraJpaRepositorio ordenRepo,
            IOrdenCompraDetalleJpaRepositorio detalleRepo,
            IStockConsumibleBodegaJpaRepositorio stockRepo,
            IMovimientoInventarioJpaRepositorio movimientoRepo,
            IEquiposJpaRepositorio equiposRepo,
            ICustodiasJpaRepositorio custodiasRepo,
            ICategoriaEquiposJpaRepositorio categoriaRepo,
            IMarcasJpaRepositorio marcasRepo,
            ICustodiosJpaRepositorio custodiosRepo,
            IMantenimientosJpaRepositorio mantenimientosRepo,
            IRecepcionLoteJpaRepositorio recepcionLoteRepo) {
        this.bodegaRepo = bodegaRepo;
        this.consumibleRepo = consumibleRepo;
        this.ordenRepo = ordenRepo;
        this.detalleRepo = detalleRepo;
        this.stockRepo = stockRepo;
        this.movimientoRepo = movimientoRepo;
        this.equiposRepo = equiposRepo;
        this.custodiasRepo = custodiasRepo;
        this.categoriaRepo = categoriaRepo;
        this.marcasRepo = marcasRepo;
        this.custodiosRepo = custodiosRepo;
        this.mantenimientosRepo = mantenimientosRepo;
        this.recepcionLoteRepo = recepcionLoteRepo;
    }

    public List<BodegaResponseDTO> listarBodegas() {
        return bodegaRepo.findAll().stream().map(this::toBodegaResponse).toList();
    }

    @Transactional
    public BodegaResponseDTO crearBodega(BodegaRequestDTO request) {
        String codigo = normalizarCodigo(request.getCodigo());
        if (bodegaRepo.existsByCodigoIgnoreCase(codigo)) {
            throw new IllegalArgumentException("Ya existe una bodega con codigo " + codigo);
        }
        BodegaJpa bodega = new BodegaJpa();
        aplicarBodega(request, bodega);
        bodega.setCodigo(codigo);
        return toBodegaResponse(bodegaRepo.save(bodega));
    }

    public List<ConsumibleResponseDTO> listarConsumibles() {
        return consumibleRepo.findAll().stream().map(this::toConsumibleResponse).toList();
    }

    @Transactional
    public ConsumibleResponseDTO crearConsumible(ConsumibleRequestDTO request) {
        String codigo = normalizarCodigo(request.getCodigo());
        if (consumibleRepo.existsByCodigoIgnoreCase(codigo)) {
            throw new IllegalArgumentException("Ya existe un consumible con codigo " + codigo);
        }
        ConsumibleJpa consumible = new ConsumibleJpa();
        consumible.setCodigo(codigo);
        aplicarConsumible(request, consumible);
        return toConsumibleResponse(consumibleRepo.save(consumible));
    }

    @Transactional
    public ConsumibleResponseDTO actualizarConsumible(Integer id, ConsumibleRequestDTO request) {
        ConsumibleJpa consumible = buscarConsumible(id);
        String codigo = normalizarCodigo(request.getCodigo());
        consumibleRepo.findByCodigoIgnoreCase(codigo)
                .filter(existente -> !existente.getIdConsumible().equals(id))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe un consumible con codigo " + codigo);
                });
        consumible.setCodigo(codigo);
        aplicarConsumible(request, consumible);
        return toConsumibleResponse(consumibleRepo.save(consumible));
    }

    @Transactional
    public ConsumibleResponseDTO cambiarEstadoConsumible(Integer id, boolean estado) {
        ConsumibleJpa consumible = buscarConsumible(id);
        consumible.setEstado(estado);
        return toConsumibleResponse(consumibleRepo.save(consumible));
    }

    public List<OrdenCompraResponseDTO> listarOrdenesCompra() {
        return ordenRepo.findAll().stream().map(this::toOrdenResponse).toList();
    }

    public OrdenCompraResponseDTO obtenerOrdenCompra(Integer id) {
        return toOrdenResponse(buscarOrden(id));
    }

    public List<RecepcionLoteResponseDTO> listarRecepcionesPorOrden(Integer idOrdenCompra) {
        buscarOrden(idOrdenCompra);
        return recepcionLoteRepo.findByOrdenCompra_IdOrdenCompra(idOrdenCompra)
                .stream().map(RecepcionLoteResponseDTO::from).toList();
    }

    @Transactional
    public RecepcionLoteResponseDTO registrarRecepcionStockPorDetalle(Integer idOC, Integer idDetalle,
            RegistrarRecepcionStockRequestDTO request) {
        OrdenCompraJpa orden = buscarOrden(idOC);
        OrdenCompraDetalleJpa detalle = buscarDetalleDeOrden(idOC, idDetalle);
        if (detalle.getTipoItem() == TipoItemInventario.ACTIVO) {
            throw new IllegalArgumentException("Este detalle es de tipo ACTIVO; use el endpoint de recepcion de activo");
        }
        int recibidoActual = detalle.getCantidadRecibida() == null ? 0 : detalle.getCantidadRecibida();
        int pendiente = detalle.getCantidadSolicitada() - recibidoActual;
        if (request.getCantidad() > pendiente) {
            throw new IllegalArgumentException("La cantidad excede el pendiente: " + pendiente);
        }
        BodegaJpa bodega = buscarBodegaActiva(request.getIdBodegaDestino());
        if (detalle.getConsumible() != null) {
            StockConsumibleBodegaJpa stock = stockRepo
                    .findByBodega_IdBodegaAndConsumible_IdConsumible(bodega.getIdBodega(),
                            detalle.getConsumible().getIdConsumible())
                    .orElseGet(() -> {
                        StockConsumibleBodegaJpa nuevo = new StockConsumibleBodegaJpa();
                        nuevo.setBodega(bodega);
                        nuevo.setConsumible(detalle.getConsumible());
                        nuevo.setCantidad(0);
                        return nuevo;
                    });
            stock.setCantidad(stock.getCantidad() + request.getCantidad());
            stockRepo.save(stock);
            registrarMovimiento(TipoMovimientoInventario.INGRESO_CONSUMIBLE, null, detalle.getConsumible(),
                    request.getCantidad(), null, bodega, null, orden, null, null, request.getObservacion(), null, null, null, null);
        }
        detalle.setCantidadRecibida(recibidoActual + request.getCantidad());
        actualizarEstadoDetalle(detalle);
        detalleRepo.save(detalle);
        actualizarEstadoOrden(orden);
        RecepcionLoteJpa lote = crearLote(orden, detalle, request.getCantidad(), detalle.getTipoItem(),
                bodega, request.getRecepcionadoPor(), request.getObservacion());
        return RecepcionLoteResponseDTO.from(lote);
    }

    @Transactional
    public RecepcionLoteResponseDTO registrarRecepcionActivoPorDetalle(Integer idOC, Integer idDetalle,
            RegistrarRecepcionActivoRequestDTO request) {
        OrdenCompraJpa orden = buscarOrden(idOC);
        OrdenCompraDetalleJpa detalle = buscarDetalleDeOrden(idOC, idDetalle);
        if (detalle.getTipoItem() != TipoItemInventario.ACTIVO) {
            throw new IllegalArgumentException("Este detalle no es de tipo ACTIVO");
        }
        int recibidoActual = detalle.getCantidadRecibida() == null ? 0 : detalle.getCantidadRecibida();
        if (recibidoActual + 1 > detalle.getCantidadSolicitada()) {
            throw new IllegalArgumentException("La recepcion excede la cantidad solicitada: " + detalle.getCantidadSolicitada());
        }
        BodegaJpa bodega = buscarBodegaActiva(request.getIdBodegaDestino());
        CategoriaEquiposJpa categoria = buscarCategoria(request.getIdCategoria());
        MarcasJpa marca = buscarMarca(request.getIdMarca());
        if (equiposRepo.existsBySerialIgnoreCase(request.getSerial())) {
            throw new IllegalArgumentException("Ya existe un activo con serial " + request.getSerial());
        }
        if (request.getMac() != null && !request.getMac().isBlank()
                && equiposRepo.existsByMacIgnoreCase(request.getMac())) {
            throw new IllegalArgumentException("Ya existe un activo con MAC " + request.getMac());
        }
        String codigoCresio = generarCodigoCresio(categoria);
        EquiposJpa equipo = new EquiposJpa();
        equipo.setCodigoSap(codigoCresio);
        equipo.setCodigoCresio(codigoCresio);
        equipo.setModelo(request.getModelo());
        equipo.setSerial(request.getSerial());
        equipo.setProcesador(request.getProcesador());
        equipo.setMemoriaRamGb(request.getMemoriaRamGb());
        equipo.setCapacidadAlmacenamientoGb(request.getCapacidadAlmacenamientoGb());
        equipo.setLicenciaWindowsActivada(request.getLicenciaWindowsActivada());
        equipo.setMac(request.getMac());
        equipo.setFechaCompra(request.getFechaCompra() == null ? LocalDate.now() : request.getFechaCompra());
        equipo.setFechaAdquisicion(equipo.getFechaCompra());
        equipo.setFechaGarantia(request.getFechaGarantia());
        equipo.setPrecioCompra(request.getPrecioCompra());
        equipo.setEstadoEquipo("OPERATIVO");
        equipo.setEstadoInventario(EstadoInventarioActivo.EN_BODEGA.name());
        equipo.setObservacionEquipo(request.getObservacion());
        equipo.setEstado(true);
        equipo.setFkCategoria(categoria);
        equipo.setFkMarcas(marca);
        equipo.setBodegaActual(bodega);
        equipo.setOrdenCompra(orden);
        equipo.setDetalleOc(detalle);
        equipo.setEtiquetado(Boolean.TRUE.equals(request.getEtiquetado()));
        EquiposJpa guardado = equiposRepo.save(equipo);
        detalle.setCantidadRecibida(recibidoActual + 1);
        actualizarEstadoDetalle(detalle);
        detalleRepo.save(detalle);
        actualizarEstadoOrden(orden);
        RecepcionLoteJpa lote = crearLote(orden, detalle, 1, TipoItemInventario.ACTIVO,
                bodega, request.getRecepcionadoPor(), request.getObservacion());
        registrarMovimiento(TipoMovimientoInventario.INGRESO_ACTIVO, guardado, null, 1, null, bodega, null, orden,
                null, EstadoInventarioActivo.EN_BODEGA.name(), request.getObservacion(),
                request.getCondicionAlRecibir(), request.getRecepcionadoPor(), null, LocalDate.now());
        return RecepcionLoteResponseDTO.from(lote);
    }

    @Transactional
    public OrdenCompraResponseDTO confirmarRecepcionOrden(Integer ordenCompraId) {
        OrdenCompraJpa orden = buscarOrden(ordenCompraId);
        validarOrdenCompleta(orden);
        orden.setFechaRecepcion(LocalDate.now());
        orden.setEstado(EstadoOrdenCompra.RECIBIDA);
        return toOrdenResponse(ordenRepo.save(orden));
    }

    @Transactional
    public OrdenCompraResponseDTO cancelarOrdenCompra(Integer ordenCompraId) {
        OrdenCompraJpa orden = buscarOrden(ordenCompraId);
        if (orden.getEstado() == EstadoOrdenCompra.RECIBIDA) {
            throw new IllegalArgumentException("No se puede cancelar una orden que ya fue recibida completamente");
        }
        if (orden.getEstado() == EstadoOrdenCompra.CANCELADA) {
            throw new IllegalArgumentException("La orden de compra ya se encuentra cancelada");
        }
        orden.setEstado(EstadoOrdenCompra.CANCELADA);
        return toOrdenResponse(ordenRepo.save(orden));
    }

    @Transactional
    public OrdenCompraResponseDTO crearOrdenCompra(OrdenCompraRequestDTO request) {
        String numeroOc = request.getNumeroOc().trim().toUpperCase(Locale.ROOT);
        if (ordenRepo.existsByNumeroOcIgnoreCase(numeroOc)) {
            throw new IllegalArgumentException("Ya existe una orden de compra con numero " + numeroOc);
        }
        BodegaJpa bodega = buscarBodegaActiva(request.getBodegaDestinoId());
        OrdenCompraJpa orden = new OrdenCompraJpa();
        orden.setNumeroOc(numeroOc);
        orden.setProveedor(request.getProveedor());
        orden.setFechaEmision(request.getFechaEmision() == null ? LocalDate.now() : request.getFechaEmision());
        orden.setObservacion(request.getObservacion());
        orden.setEstado(EstadoOrdenCompra.EMITIDA);
        orden.setBodegaDestino(bodega);
        OrdenCompraJpa guardada = ordenRepo.save(orden);

        Set<Integer> consumiblesEnEstaOC = new HashSet<>();
        for (OrdenCompraDetalleRequestDTO detalleRequest : request.getDetalles()) {
            if (detalleRequest.getConsumibleId() != null) {
                if (!consumiblesEnEstaOC.add(detalleRequest.getConsumibleId())) {
                    throw new IllegalArgumentException(
                            "El consumible ya existe en otra linea de esta orden. Consolide las cantidades en una sola linea.");
                }
            }
            OrdenCompraDetalleJpa detalle = new OrdenCompraDetalleJpa();
            detalle.setOrdenCompra(guardada);
            detalle.setTipoItem(detalleRequest.getTipoItem());
            detalle.setDescripcion(detalleRequest.getDescripcion());
            detalle.setCantidadSolicitada(detalleRequest.getCantidadSolicitada());
            detalle.setCantidadRecibida(0);
            if (detalleRequest.getCategoriaId() != null) {
                detalle.setCategoria(buscarCategoria(detalleRequest.getCategoriaId()));
            }
            if (detalleRequest.getMarcaId() != null) {
                detalle.setMarca(buscarMarca(detalleRequest.getMarcaId()));
            }
            if (detalleRequest.getConsumibleId() != null) {
                detalle.setConsumible(buscarConsumibleActivo(detalleRequest.getConsumibleId()));
            }
            detalleRepo.save(detalle);
        }
        return toOrdenResponse(guardada);
    }

    public List<StockConsumibleResponseDTO> listarStockPorBodega(Integer bodegaId) {
        return stockRepo.findByBodega_IdBodega(bodegaId).stream().map(this::toStockResponse).toList();
    }

    public List<MovimientoInventarioResponseDTO> listarMovimientosRecientes() {
        return movimientoRepo.findTop100ByOrderByFechaMovimientoDesc().stream()
                .map(this::toMovimientoResponse)
                .toList();
    }

    public List<ActivoInventarioResponseDTO> listarActivosEnBodega() {
        return equiposRepo.findByEstadoInventarioAndEstadoTrue(EstadoInventarioActivo.EN_BODEGA.name())
                .stream()
                .map(this::toActivoResponse)
                .toList();
    }

    public List<ActivoInventarioResponseDTO> listarActivosPorEstado(String estadoInventario) {
        return equiposRepo.findByEstadoInventarioAndEstadoTrue(estadoInventario)
                .stream()
                .map(this::toActivoResponse)
                .toList();
    }

    @Transactional
    public ActivoInventarioResponseDTO asignarActivo(AsignacionActivoRequestDTO request) {
        AsignacionLoteRequestDTO lote = new AsignacionLoteRequestDTO();
        lote.setEquipoIds(List.of(request.getEquipoId()));
        lote.setCustodioId(request.getCustodioId());
        lote.setFechaInicio(request.getFechaInicio());
        lote.setCondicionEntrega(request.getCondicionEntrega());
        lote.setRealizadoPor(request.getRealizadoPor());
        lote.setObservacion(request.getObservacion());
        return asignarActivosLote(lote).getActivos().get(0);
    }

    @Transactional
    public StockConsumibleResponseDTO asignarConsumible(AsignacionConsumibleRequestDTO request) {
        BodegaJpa bodega = buscarBodega(request.getBodegaId());
        ConsumibleJpa consumible = buscarConsumibleActivo(request.getConsumibleId());
        CustodiosJpa custodio = buscarCustodio(request.getCustodioId());

        StockConsumibleBodegaJpa stock = stockRepo
                .findByBodega_IdBodegaAndConsumible_IdConsumible(bodega.getIdBodega(), consumible.getIdConsumible())
                .orElseThrow(() -> new IllegalArgumentException("No existe stock del consumible en la bodega"));

        int disponible = stock.getCantidad() == null ? 0 : stock.getCantidad();
        if (disponible < request.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + disponible);
        }

        stock.setCantidad(disponible - request.getCantidad());
        StockConsumibleBodegaJpa guardado = stockRepo.save(stock);

        registrarMovimiento(TipoMovimientoInventario.ASIGNACION_CONSUMIBLE, null, consumible, request.getCantidad(),
                bodega, null, custodio, null, null, null, request.getObservacion(), null, null, null, null);
        return toStockResponse(guardado);
    }

    @Transactional
    public ActivoInventarioResponseDTO devolverActivo(DevolucionActivoRequestDTO request) {
        EquiposJpa equipo = buscarEquipo(request.getEquipoId());
        BodegaJpa bodega = buscarBodega(request.getBodegaId());
        String estadoAnterior = equipo.getEstadoInventario();
        EstadoInventarioActivo estadoDestino = resolverEstadoDevolucion(request.getEstadoInventarioDestino());

        CustodiasJpa custodia = custodiasRepo
                .findFirstByFkEquipo_IdEquipoAndEstadoTrueAndFechaFinIsNullOrderByIdCustodiaEquipoDesc(equipo.getIdEquipo())
                .orElseThrow(() -> new IllegalArgumentException("El activo no tiene una custodia activa"));
        CustodiosJpa custodio = custodia.getFkCustodio();

        custodia.setEstado(false);
        custodia.setFechaFin(request.getFechaDevolucion() == null ? LocalDate.now() : request.getFechaDevolucion());
        custodia.setObservacion(unirObservacion(custodia.getObservacion(), request.getObservacion()));
        custodiasRepo.save(custodia);

        equipo.setEstadoInventario(estadoDestino.name());
        equipo.setBodegaActual(bodega);
        EquiposJpa guardado = equiposRepo.save(equipo);

        registrarMovimiento(TipoMovimientoInventario.DEVOLUCION, guardado, null, 1,
                null, bodega, custodio, guardado.getOrdenCompra(), estadoAnterior, estadoDestino.name(),
                request.getObservacion(), request.getEstadoFisicoRetorno(), request.getRealizadoPor(),
                request.getMotivo(),
                request.getFechaDevolucion() != null ? request.getFechaDevolucion() : LocalDate.now());
        return toActivoResponse(guardado);
    }

    @Transactional
    public StockConsumibleResponseDTO devolverConsumible(DevolucionConsumibleRequestDTO request) {
        BodegaJpa bodega = buscarBodega(request.getBodegaId());
        ConsumibleJpa consumible = buscarConsumible(request.getConsumibleId());
        CustodiosJpa custodio = buscarCustodio(request.getCustodioId());

        StockConsumibleBodegaJpa stock = stockRepo
                .findByBodega_IdBodegaAndConsumible_IdConsumible(bodega.getIdBodega(), consumible.getIdConsumible())
                .orElseGet(() -> {
                    StockConsumibleBodegaJpa nuevo = new StockConsumibleBodegaJpa();
                    nuevo.setBodega(bodega);
                    nuevo.setConsumible(consumible);
                    nuevo.setCantidad(0);
                    return nuevo;
                });
        stock.setCantidad((stock.getCantidad() == null ? 0 : stock.getCantidad()) + request.getCantidad());
        StockConsumibleBodegaJpa guardado = stockRepo.save(stock);

        registrarMovimiento(TipoMovimientoInventario.DEVOLUCION, null, consumible, request.getCantidad(),
                null, bodega, custodio, null, null, null, request.getObservacion(), null, null, null, null);
        return toStockResponse(guardado);
    }

    @Transactional
    public ActivoInventarioResponseDTO trasladarActivo(TrasladoActivoRequestDTO request) {
        EquiposJpa equipo = buscarEquipo(request.getEquipoId());
        if (!EstadoInventarioActivo.EN_BODEGA.name().equals(equipo.getEstadoInventario())) {
            throw new IllegalArgumentException("Solo se pueden trasladar activos disponibles en bodega");
        }
        BodegaJpa origen = equipo.getBodegaActual();
        if (origen == null) {
            throw new IllegalArgumentException("El activo no tiene bodega origen");
        }
        BodegaJpa destino = buscarBodega(request.getBodegaDestinoId());
        if (origen.getIdBodega().equals(destino.getIdBodega())) {
            throw new IllegalArgumentException("La bodega destino debe ser diferente a la bodega origen");
        }

        equipo.setEstadoInventario(EstadoInventarioActivo.EN_TRANSITO.name());
        EquiposJpa guardado = equiposRepo.save(equipo);

        registrarMovimiento(TipoMovimientoInventario.TRASLADO, guardado, null, 1,
                origen, destino, null, guardado.getOrdenCompra(), EstadoInventarioActivo.EN_BODEGA.name(),
                EstadoInventarioActivo.EN_TRANSITO.name(), request.getObservacion(), null, null, null, null);
        return toActivoResponse(guardado);
    }

    @Transactional
    public ActivoInventarioResponseDTO confirmarLlegadaActivo(ConfirmarLlegadaActivoRequestDTO request) {
        EquiposJpa equipo = buscarEquipo(request.getEquipoId());
        if (!EstadoInventarioActivo.EN_TRANSITO.name().equals(equipo.getEstadoInventario())) {
            throw new IllegalArgumentException("Solo se pueden confirmar activos que esten EN_TRANSITO");
        }
        BodegaJpa origen = equipo.getBodegaActual();
        BodegaJpa destino = buscarBodega(request.getBodegaDestinoId());
        equipo.setBodegaActual(destino);
        equipo.setEstadoInventario(EstadoInventarioActivo.EN_BODEGA.name());
        EquiposJpa guardado = equiposRepo.save(equipo);
        registrarMovimiento(TipoMovimientoInventario.TRASLADO, guardado, null, 1,
                origen, destino, null, guardado.getOrdenCompra(),
                EstadoInventarioActivo.EN_TRANSITO.name(), EstadoInventarioActivo.EN_BODEGA.name(),
                request.getObservacion(), null, null, null, null);
        return toActivoResponse(guardado);
    }

    @Transactional
    public StockConsumibleResponseDTO trasladarConsumible(TrasladoConsumibleRequestDTO request) {
        BodegaJpa origen = buscarBodegaActiva(request.getBodegaOrigenId());
        BodegaJpa destino = buscarBodegaActiva(request.getBodegaDestinoId());
        if (origen.getIdBodega().equals(destino.getIdBodega())) {
            throw new IllegalArgumentException("La bodega destino debe ser diferente a la bodega origen");
        }
        ConsumibleJpa consumible = buscarConsumibleActivo(request.getConsumibleId());

        StockConsumibleBodegaJpa stockOrigen = stockRepo
                .findByBodega_IdBodegaAndConsumible_IdConsumible(origen.getIdBodega(), consumible.getIdConsumible())
                .orElseThrow(() -> new IllegalArgumentException("No existe stock del consumible en la bodega origen"));
        int disponible = stockOrigen.getCantidad() == null ? 0 : stockOrigen.getCantidad();
        if (disponible < request.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente en origen. Disponible: " + disponible);
        }
        stockOrigen.setCantidad(disponible - request.getCantidad());
        stockRepo.save(stockOrigen);

        StockConsumibleBodegaJpa stockDestino = stockRepo
                .findByBodega_IdBodegaAndConsumible_IdConsumible(destino.getIdBodega(), consumible.getIdConsumible())
                .orElseGet(() -> {
                    StockConsumibleBodegaJpa nuevo = new StockConsumibleBodegaJpa();
                    nuevo.setBodega(destino);
                    nuevo.setConsumible(consumible);
                    nuevo.setCantidad(0);
                    return nuevo;
                });
        stockDestino.setCantidad((stockDestino.getCantidad() == null ? 0 : stockDestino.getCantidad())
                + request.getCantidad());
        StockConsumibleBodegaJpa guardadoDestino = stockRepo.save(stockDestino);

        registrarMovimiento(TipoMovimientoInventario.TRASLADO, null, consumible, request.getCantidad(),
                origen, destino, null, null, null, null, request.getObservacion(), null, null, null, null);
        return toStockResponse(guardadoDestino);
    }

    @Transactional
    public ActivoInventarioResponseDTO darBajaActivo(BajaActivoRequestDTO request) {
        EquiposJpa equipo = buscarEquipo(request.getEquipoId());
        if (EstadoInventarioActivo.DADO_DE_BAJA.name().equals(equipo.getEstadoInventario())) {
            throw new IllegalArgumentException("El activo ya se encuentra dado de baja");
        }
        if (mantenimientosRepo.existsByEquipoEnProcesoIncluyendoMultiple(
                equipo.getIdEquipo(), EstadoInternoMantenimiento.EN_PROCESO)) {
            throw new IllegalArgumentException("No se puede dar de baja un activo con mantenimiento en proceso");
        }

        String estadoAnterior = equipo.getEstadoInventario();
        BodegaJpa bodegaOrigen = equipo.getBodegaActual();
        CustodiosJpa custodio = null;

        var custodiaActiva = custodiasRepo
                .findFirstByFkEquipo_IdEquipoAndEstadoTrueAndFechaFinIsNullOrderByIdCustodiaEquipoDesc(equipo.getIdEquipo());
        if (custodiaActiva.isPresent()) {
            CustodiasJpa custodia = custodiaActiva.get();
            custodio = custodia.getFkCustodio();
            custodia.setEstado(false);
            custodia.setFechaFin(request.getFechaBaja() == null ? LocalDate.now() : request.getFechaBaja());
            custodia.setObservacion(unirObservacion(custodia.getObservacion(), observacionBaja(request)));
            custodiasRepo.save(custodia);
        }

        equipo.setEstadoInventario(EstadoInventarioActivo.DADO_DE_BAJA.name());
        equipo.setEstadoEquipo("DADO_DE_BAJA");
        equipo.setBodegaActual(null);
        equipo.setObservacionEquipo(unirObservacion(equipo.getObservacionEquipo(), observacionBaja(request)));
        EquiposJpa guardado = equiposRepo.save(equipo);

        registrarMovimiento(TipoMovimientoInventario.BAJA, guardado, null, 1,
                bodegaOrigen, null, custodio, guardado.getOrdenCompra(), estadoAnterior,
                EstadoInventarioActivo.DADO_DE_BAJA.name(), observacionBaja(request),
                null, request.getAutorizadoPor(), request.getMotivo(),
                request.getFechaBaja() != null ? request.getFechaBaja() : LocalDate.now());
        return toActivoResponse(guardado);
    }

    @Transactional
    public ActivoInventarioResponseDTO enviarAReparacion(EnviarReparacionRequestDTO request) {
        EquiposJpa equipo = buscarEquipo(request.getEquipoId());
        String estadoActual = equipo.getEstadoInventario();
        if (EstadoInventarioActivo.EN_REPARACION.name().equals(estadoActual)) {
            throw new IllegalArgumentException("El activo ya se encuentra en reparacion");
        }
        if (EstadoInventarioActivo.DADO_DE_BAJA.name().equals(estadoActual)) {
            throw new IllegalArgumentException("No se puede enviar a reparacion un activo dado de baja");
        }

        BodegaJpa bodegaOrigen = equipo.getBodegaActual();
        CustodiosJpa custodio = null;

        // Si esta asignado, cerrar la custodia activa
        var custodiaActiva = custodiasRepo
                .findFirstByFkEquipo_IdEquipoAndEstadoTrueAndFechaFinIsNullOrderByIdCustodiaEquipoDesc(equipo.getIdEquipo());
        if (custodiaActiva.isPresent()) {
            CustodiasJpa custodia = custodiaActiva.get();
            custodio = custodia.getFkCustodio();
            custodia.setEstado(false);
            custodia.setFechaFin(request.getFechaEnvio() != null ? request.getFechaEnvio() : LocalDate.now());
            custodia.setObservacion(unirObservacion(custodia.getObservacion(),
                    "Enviado a reparacion: " + (request.getMotivo() != null ? request.getMotivo() : "")));
            custodiasRepo.save(custodia);
        }

        equipo.setEstadoInventario(EstadoInventarioActivo.EN_REPARACION.name());
        equipo.setBodegaActual(null);
        EquiposJpa guardado = equiposRepo.save(equipo);

        registrarMovimiento(TipoMovimientoInventario.REPARACION, guardado, null, 1,
                bodegaOrigen, null, custodio, guardado.getOrdenCompra(), estadoActual,
                EstadoInventarioActivo.EN_REPARACION.name(), request.getObservacion(),
                null, request.getProveedorTecnico(), request.getMotivo(),
                request.getFechaEnvio() != null ? request.getFechaEnvio() : LocalDate.now());
        return toActivoResponse(guardado);
    }

    @Transactional
    public ActivoInventarioResponseDTO retornarDeReparacion(RetornarReparacionRequestDTO request) {
        EquiposJpa equipo = buscarEquipo(request.getEquipoId());
        if (!EstadoInventarioActivo.EN_REPARACION.name().equals(equipo.getEstadoInventario())) {
            throw new IllegalArgumentException("El activo no se encuentra en reparacion");
        }
        BodegaJpa bodegaDestino = buscarBodegaActiva(request.getBodegaDestinoId());

        equipo.setEstadoInventario(EstadoInventarioActivo.EN_BODEGA.name());
        equipo.setBodegaActual(bodegaDestino);
        if (request.getCondicion() != null && !request.getCondicion().isBlank()) {
            equipo.setCondicionAlRecibir(request.getCondicion());
        }
        EquiposJpa guardado = equiposRepo.save(equipo);

        registrarMovimiento(TipoMovimientoInventario.REPARACION, guardado, null, 1,
                null, bodegaDestino, null, guardado.getOrdenCompra(),
                EstadoInventarioActivo.EN_REPARACION.name(), EstadoInventarioActivo.EN_BODEGA.name(),
                request.getObservacion(), request.getCondicion(), null, "RETORNO_REPARACION",
                request.getFechaRetorno() != null ? request.getFechaRetorno() : LocalDate.now());
        return toActivoResponse(guardado);
    }

    private void aplicarBodega(BodegaRequestDTO request, BodegaJpa bodega) {
        bodega.setNombre(request.getNombre());
        bodega.setCiudad(request.getCiudad());
        bodega.setDireccion(request.getDireccion());
        bodega.setEstado(request.getEstado() == null || request.getEstado());
        if (request.getCustodioResponsableId() != null) {
            CustodiosJpa custodio = custodiosRepo.findById(request.getCustodioResponsableId())
                    .orElseThrow(() -> new IllegalArgumentException("Custodio no encontrado"));
            bodega.setCustodioResponsable(custodio);
        }
    }

    private void aplicarConsumible(ConsumibleRequestDTO request, ConsumibleJpa consumible) {
        consumible.setNombre(request.getNombre());
        consumible.setDescripcion(request.getDescripcion());
        consumible.setUnidadMedida(request.getUnidadMedida());
        consumible.setEstado(request.getEstado() == null || request.getEstado());
    }

    private String generarCodigoCresio(CategoriaEquiposJpa categoria) {
        String prefijo = "CR-" + prefijoCategoria(categoria.getNombre()) + "-";
        String ultimoCodigo = equiposRepo.findFirstByCodigoCresioStartingWithOrderByCodigoCresioDesc(prefijo)
                .map(EquiposJpa::getCodigoCresio)
                .orElse(null);
        int siguiente = 1;
        if (ultimoCodigo != null && ultimoCodigo.length() >= prefijo.length() + 4) {
            String secuencia = ultimoCodigo.substring(prefijo.length());
            try {
                siguiente = Integer.parseInt(secuencia) + 1;
            } catch (NumberFormatException ignored) {
                siguiente = 1;
            }
        }
        String codigo = prefijo + String.format("%04d", siguiente);
        while (equiposRepo.existsByCodigoCresioIgnoreCase(codigo)) {
            siguiente++;
            codigo = prefijo + String.format("%04d", siguiente);
        }
        return codigo;
    }

    private String prefijoCategoria(String nombre) {
        String normalizado = Normalizer.normalize(nombre == null ? "ACT" : nombre, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
        if (normalizado.contains("LAP")) return "LAP";
        if (normalizado.contains("DESK") || normalizado.contains("CPU")) return "DSK";
        if (normalizado.contains("IMP")) return "IMP";
        if (normalizado.contains("SERV")) return "SRV";
        if (normalizado.contains("RED") || normalizado.contains("SWITCH") || normalizado.contains("ROUT")) return "NET";
        if (normalizado.contains("TAB")) return "TAB";
        if (normalizado.contains("UPS")) return "UPS";
        if (normalizado.contains("TEL")) return "TEL";
        if (normalizado.contains("CAM")) return "CAM";
        String soloLetras = normalizado.replaceAll("[^A-Z]", "");
        return soloLetras.length() >= 3 ? soloLetras.substring(0, 3) : "ACT";
    }

    private void validarOrdenCompleta(OrdenCompraJpa orden) {
        List<OrdenCompraDetalleJpa> detalles = detalleRepo.findByOrdenCompra_IdOrdenCompra(orden.getIdOrdenCompra());
        if (detalles.isEmpty()) {
            throw new IllegalArgumentException("La orden de compra no tiene detalles");
        }
        boolean incompleta = detalles.stream().anyMatch(d ->
                d.getEstado() != EstadoOrdenCompraDetalle.COMPLETO
                && d.getEstado() != EstadoOrdenCompraDetalle.CANCELADO);
        if (incompleta) {
            throw new IllegalArgumentException("No se puede cerrar la orden: aun existen items pendientes de recepcion");
        }
    }

    private void actualizarEstadoDetalle(OrdenCompraDetalleJpa detalle) {
        int recibido = detalle.getCantidadRecibida() == null ? 0 : detalle.getCantidadRecibida();
        if (recibido >= detalle.getCantidadSolicitada()) {
            detalle.setEstado(EstadoOrdenCompraDetalle.COMPLETO);
        } else if (recibido > 0) {
            detalle.setEstado(EstadoOrdenCompraDetalle.PARCIAL);
        }
    }

    private void actualizarEstadoOrden(OrdenCompraJpa orden) {
        List<OrdenCompraDetalleJpa> detalles = detalleRepo.findByOrdenCompra_IdOrdenCompra(orden.getIdOrdenCompra());
        boolean todoCompleto = !detalles.isEmpty()
                && detalles.stream().allMatch(d -> d.getEstado() == EstadoOrdenCompraDetalle.COMPLETO);
        if (todoCompleto) {
            orden.setEstado(EstadoOrdenCompra.RECIBIDA);
            orden.setFechaRecepcion(LocalDate.now());
        } else {
            if (orden.getEstado() != EstadoOrdenCompra.RECIBIDA) {
                orden.setEstado(EstadoOrdenCompra.RECEPCION_PARCIAL);
            }
            if (orden.getFechaRecepcion() == null) {
                orden.setFechaRecepcion(LocalDate.now());
            }
        }
        ordenRepo.save(orden);
    }

    private OrdenCompraDetalleJpa buscarDetalleDeOrden(Integer idOC, Integer idDetalle) {
        OrdenCompraDetalleJpa detalle = detalleRepo.findById(idDetalle)
                .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado: " + idDetalle));
        if (!detalle.getOrdenCompra().getIdOrdenCompra().equals(idOC)) {
            throw new IllegalArgumentException("El detalle no pertenece a la orden de compra indicada");
        }
        return detalle;
    }

    private RecepcionLoteJpa crearLote(OrdenCompraJpa orden, OrdenCompraDetalleJpa detalle,
            Integer cantidad, TipoItemInventario tipoItem, BodegaJpa bodega,
            String recepcionadoPor, String observacion) {
        RecepcionLoteJpa lote = new RecepcionLoteJpa();
        lote.setOrdenCompra(orden);
        lote.setOrdenCompraDetalle(detalle);
        lote.setFechaRecepcion(LocalDateTime.now());
        lote.setCantidadRecibida(cantidad);
        lote.setTipoItem(tipoItem);
        lote.setEstado(EstadoRecepcionLote.REGISTRADO);
        lote.setObservacion(observacion);
        lote.setBodegaDestino(bodega);
        lote.setRecepcionadoPor(recepcionadoPor);
        lote.setRecepcionadoEn(LocalDateTime.now());
        lote.setUuid(UUID.randomUUID().toString());
        return recepcionLoteRepo.save(lote);
    }

    @Transactional(readOnly = true)
    public List<ActivoInventarioResponseDTO> listarActivosEnTransito() {
        return equiposRepo.findByEstadoInventarioAndEstadoTrue(EstadoInventarioActivo.EN_TRANSITO.name()).stream()
                .map(equipo -> {
                    ActivoInventarioResponseDTO dto = toActivoResponse(equipo);
                    movimientoRepo.findFirstByEquipo_IdEquipoAndEstadoNuevoOrderByFechaMovimientoDesc(
                                    equipo.getIdEquipo(), EstadoInventarioActivo.EN_TRANSITO.name())
                            .ifPresent(m -> {
                                if (m.getBodegaDestino() != null) {
                                    dto.setBodegaDestinoId(m.getBodegaDestino().getIdBodega());
                                    dto.setBodegaDestinoNombre(m.getBodegaDestino().getNombre());
                                }
                                dto.setDespachadorNombre(m.getRealizadoPor());
                                dto.setFechaSalida(m.getFechaMovimiento() != null
                                        ? m.getFechaMovimiento().toString() : null);
                            });
                    return dto;
                }).toList();
    }

    @Transactional(readOnly = true)
    public Page<MovimientoInventarioResponseDTO> buscarMovimientosPaginados(
            int page, int size, String tipo, String fechaDesde, String fechaHasta, String equipoCodigo) {
        size = Math.min(Math.max(size, 1), 200);
        Pageable pageable = PageRequest.of(Math.max(page, 0), size, Sort.by("fechaMovimiento").descending());
        Specification<MovimientoInventarioJpa> spec = Specification.where((Specification<MovimientoInventarioJpa>) null);
        if (tipo != null && !tipo.isBlank()) {
            try {
                TipoMovimientoInventario tipoEnum = TipoMovimientoInventario.valueOf(tipo.trim().toUpperCase(Locale.ROOT));
                spec = spec.and((root, q, cb) -> cb.equal(root.get("tipoMovimiento"), tipoEnum));
            } catch (IllegalArgumentException ignored) {}
        }
        if (fechaDesde != null && !fechaDesde.isBlank()) {
            try {
                LocalDateTime desde = LocalDate.parse(fechaDesde.trim()).atStartOfDay();
                spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("fechaMovimiento"), desde));
            } catch (Exception ignored) {}
        }
        if (fechaHasta != null && !fechaHasta.isBlank()) {
            try {
                LocalDateTime hasta = LocalDate.parse(fechaHasta.trim()).plusDays(1).atStartOfDay();
                spec = spec.and((root, q, cb) -> cb.lessThan(root.get("fechaMovimiento"), hasta));
            } catch (Exception ignored) {}
        }
        if (equipoCodigo != null && !equipoCodigo.isBlank()) {
            String patron = "%" + equipoCodigo.toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, q, cb) -> {
                var eq = root.join("equipo", jakarta.persistence.criteria.JoinType.LEFT);
                return cb.or(
                        cb.like(cb.lower(eq.get("codigoCresio")), patron),
                        cb.like(cb.lower(eq.get("codigoSap")), patron));
            });
        }
        return movimientoRepo.findAll(spec, pageable).map(this::toMovimientoResponse);
    }

    @Transactional
    public AsignacionActivosResponseDTO asignarActivosLote(AsignacionLoteRequestDTO request) {
        if (request.getEquipoIds() == null || request.getEquipoIds().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar al menos un activo para asignar");
        }
        CustodiosJpa custodio = buscarColaboradorActivo(request.getCustodioId());
        List<EquiposJpa> equipos = new ArrayList<>();
        for (Integer idEquipo : request.getEquipoIds()) {
            EquiposJpa equipo = buscarEquipo(idEquipo);
            if (!EstadoInventarioActivo.EN_BODEGA.name().equals(equipo.getEstadoInventario())) {
                throw new IllegalArgumentException(
                        "El activo " + (equipo.getCodigoCresio() != null ? equipo.getCodigoCresio() : idEquipo)
                        + " no está en bodega disponible");
            }
            if (!Boolean.TRUE.equals(equipo.getEtiquetado())) {
                throw new IllegalArgumentException(
                        "El activo " + (equipo.getCodigoCresio() != null ? equipo.getCodigoCresio() : idEquipo)
                        + " no tiene etiqueta física asignada");
            }
            if (custodiasRepo.existsByFkEquipo_IdEquipoAndEstadoTrue(equipo.getIdEquipo())) {
                throw new IllegalArgumentException(
                        "El activo " + (equipo.getCodigoCresio() != null ? equipo.getCodigoCresio() : idEquipo)
                        + " ya tiene una custodia activa");
            }
            if (equipo.getBodegaActual() == null) {
                throw new IllegalArgumentException(
                        "El activo " + (equipo.getCodigoCresio() != null ? equipo.getCodigoCresio() : idEquipo)
                        + " no tiene bodega de origen registrada");
            }
            buscarBodegaActiva(equipo.getBodegaActual().getIdBodega());
            equipos.add(equipo);
        }
        AsignacionActivosResponseDTO response = new AsignacionActivosResponseDTO();
        List<ActivoInventarioResponseDTO> activos = new ArrayList<>();
        List<com.uisrael.gestionactivosapi.presentacion.dto.response.CustodiasResponseDTO> custodias = new ArrayList<>();
        for (EquiposJpa equipo : equipos) {
            BodegaJpa bodegaOrigen = equipo.getBodegaActual();
            String estadoAnterior = equipo.getEstadoInventario();
            CustodiasJpa custodia = new CustodiasJpa();
            custodia.setFkEquipo(equipo);
            custodia.setFkCustodio(custodio);
            custodia.setFechaInicio(request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now());
            custodia.setObservacion(request.getObservacion());
            custodia.setEstado(true);
            custodia.setTipoMovimiento("ASIGNACION");
            CustodiasJpa custodiaGuardada = custodiasRepo.save(custodia);
            equipo.setEstadoInventario(EstadoInventarioActivo.ASIGNADO.name());
            equipo.setBodegaActual(null);
            EquiposJpa guardado = equiposRepo.save(equipo);
            registrarMovimiento(TipoMovimientoInventario.ASIGNACION_ACTIVO, guardado, null, 1,
                    bodegaOrigen, null, custodio, guardado.getOrdenCompra(),
                    estadoAnterior, EstadoInventarioActivo.ASIGNADO.name(),
                    request.getObservacion(), request.getCondicionEntrega(), request.getRealizadoPor(), null,
                    request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now());
            activos.add(toActivoResponse(guardado));
            custodias.add(toCustodiaResponse(custodiaGuardada));
        }
        response.setActivos(activos);
        response.setCustodias(custodias);
        return response;
    }

    private void registrarMovimiento(TipoMovimientoInventario tipo, EquiposJpa equipo, ConsumibleJpa consumible,
            Integer cantidad, BodegaJpa origen, BodegaJpa destino, CustodiosJpa custodio, OrdenCompraJpa orden,
            String estadoAnterior, String estadoNuevo, String observacion,
            String condicion, String realizadoPor, String motivo, LocalDate fechaEfectiva) {
        MovimientoInventarioJpa movimiento = new MovimientoInventarioJpa();
        movimiento.setTipoMovimiento(tipo);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setEquipo(equipo);
        movimiento.setConsumible(consumible);
        movimiento.setCantidad(cantidad);
        movimiento.setBodegaOrigen(origen);
        movimiento.setBodegaDestino(destino);
        movimiento.setCustodio(custodio);
        movimiento.setOrdenCompra(orden);
        movimiento.setEstadoAnterior(estadoAnterior);
        movimiento.setEstadoNuevo(estadoNuevo);
        movimiento.setObservacion(observacion);
        movimiento.setCondicion(condicion);
        movimiento.setRealizadoPor(realizadoPor);
        movimiento.setMotivo(motivo);
        movimiento.setFechaEfectiva(fechaEfectiva);
        movimientoRepo.save(movimiento);
    }

    public List<ActivoInventarioResponseDTO> listarSinInventario() {
        return equiposRepo.findByEstadoInventarioIsNullAndEstadoTrue()
                .stream()
                .map(this::toActivoResponse)
                .toList();
    }

    @Transactional
    public ActivoInventarioResponseDTO adoptarInventarioInicial(Integer id, AdoptarInventarioInicialRequestDTO request) {
        EquiposJpa equipo = buscarEquipo(id);
        if (equipo.getEstadoInventario() != null) {
            String visible = equipo.getEstadoInventario().isBlank() ? "(valor vacío inválido)" : equipo.getEstadoInventario();
            throw new IllegalArgumentException("El activo ya tiene estado de inventario asignado: " + visible);
        }
        BodegaJpa bodega = buscarBodegaActiva(request.getBodegaId());

        String codigoCresio = request.getCodigoCresio() != null ? request.getCodigoCresio().trim() : null;
        if (codigoCresio != null && !codigoCresio.isBlank()) {
            if (equiposRepo.existsByCodigoCresioIgnoreCaseAndIdEquipoNot(codigoCresio, id)) {
                throw new IllegalArgumentException("El codigo " + codigoCresio + " ya esta en uso por otro activo");
            }
            equipo.setCodigoCresio(codigoCresio);
        } else if (equipo.getCodigoCresio() == null && equipo.getCodigoSap() != null) {
            equipo.setCodigoCresio(equipo.getCodigoSap());
        }

        equipo.setBodegaActual(bodega);
        equipo.setEtiquetado(Boolean.TRUE.equals(request.getEtiquetado()));
        equipo.setCondicionAlRecibir(request.getCondicionFisica());

        String obsInicial = "[INVENTARIO_INICIAL]";
        if (request.getObservacion() != null && !request.getObservacion().isBlank()) {
            obsInicial += " " + request.getObservacion();
        }
        String obsActual = equipo.getObservacionEquipo();
        equipo.setObservacionEquipo(obsActual != null ? obsActual + " " + obsInicial : obsInicial);

        if (request.getCustodioId() != null) {
            if (!Boolean.TRUE.equals(equipo.getEtiquetado())) {
                throw new IllegalArgumentException("El activo debe estar etiquetado antes de asignarse a un custodio");
            }
            CustodiosJpa custodio = buscarColaboradorActivo(request.getCustodioId());
            if (custodiasRepo.existsByFkEquipo_IdEquipoAndEstadoTrue(equipo.getIdEquipo())) {
                throw new IllegalStateException("El activo ya tiene una custodia activa.");
            }
            // Paso 1: ingresar a bodega (entidad pasa por EN_BODEGA, audit veraz)
            equipo.setEstadoInventario(EstadoInventarioActivo.EN_BODEGA.name());
            equiposRepo.save(equipo);
            registrarMovimiento(TipoMovimientoInventario.INGRESO_ACTIVO, equipo, null, 1,
                    null, bodega, null, null, null, EstadoInventarioActivo.EN_BODEGA.name(),
                    obsInicial, request.getCondicionFisica(), null, "INVENTARIO_INICIAL", LocalDate.now());

            // Paso 2: asignar al custodio desde la bodega
            equipo.setBodegaActual(null);
            equipo.setEstadoInventario(EstadoInventarioActivo.ASIGNADO.name());
            EquiposJpa guardado = equiposRepo.save(equipo);

            CustodiasJpa custodia = new CustodiasJpa();
            custodia.setFkEquipo(guardado);
            custodia.setFkCustodio(custodio);
            custodia.setFechaInicio(LocalDate.now());
            custodia.setObservacion("Inventario inicial: " + (request.getObservacion() != null ? request.getObservacion() : ""));
            custodia.setEstado(true);
            custodia.setTipoMovimiento("INVENTARIO_INICIAL");
            custodiasRepo.save(custodia);

            registrarMovimiento(TipoMovimientoInventario.ASIGNACION_ACTIVO, guardado, null, 1,
                    bodega, null, custodio, null, EstadoInventarioActivo.EN_BODEGA.name(),
                    EstadoInventarioActivo.ASIGNADO.name(), obsInicial, request.getCondicionFisica(), null, null, LocalDate.now());
            return toActivoResponse(guardado);
        } else {
            equipo.setEstadoInventario(EstadoInventarioActivo.EN_BODEGA.name());
            EquiposJpa guardado = equiposRepo.save(equipo);
            registrarMovimiento(TipoMovimientoInventario.INGRESO_ACTIVO, guardado, null, 1,
                    null, bodega, null, null, null, EstadoInventarioActivo.EN_BODEGA.name(),
                    obsInicial, request.getCondicionFisica(), null, "INVENTARIO_INICIAL", LocalDate.now());
            return toActivoResponse(guardado);
        }
    }

    @Transactional
    public ActivoInventarioResponseDTO registrarEtiqueta(Integer id, com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RegistrarEtiquetaRequestDTO request) {
        EquiposJpa equipo = buscarEquipo(id);
        if (EstadoInventarioActivo.DADO_DE_BAJA.name().equals(equipo.getEstadoInventario())) {
            throw new IllegalArgumentException("No se puede etiquetar un activo dado de baja");
        }
        String estadoAnterior = Boolean.TRUE.equals(equipo.getEtiquetado()) ? "etiquetado=true" : "etiquetado=false";
        equipo.setEtiquetado(Boolean.TRUE.equals(request.getEtiquetado()));
        if (request.getCodigoCresio() != null && !request.getCodigoCresio().isBlank()) {
            equipo.setCodigoCresio(request.getCodigoCresio().trim());
            equipo.setCodigoSap(request.getCodigoCresio().trim());
        }
        equiposRepo.save(equipo);
        return toActivoResponse(equipo);
    }

    private BodegaJpa buscarBodega(Integer id) {
        return bodegaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada"));
    }

    private BodegaJpa buscarBodegaActiva(Integer id) {
        BodegaJpa bodega = buscarBodega(id);
        if (!bodega.isEstado()) {
            throw new IllegalArgumentException("La bodega seleccionada se encuentra inactiva o fuera de servicio");
        }
        return bodega;
    }

    private ConsumibleJpa buscarConsumible(Integer id) {
        return consumibleRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Consumible no encontrado"));
    }

    private ConsumibleJpa buscarConsumibleActivo(Integer id) {
        ConsumibleJpa consumible = buscarConsumible(id);
        if (!consumible.isEstado()) {
            throw new IllegalArgumentException("No se puede operar con un consumible inactivo");
        }
        return consumible;
    }

    private OrdenCompraJpa buscarOrden(Integer id) {
        return ordenRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Orden de compra no encontrada"));
    }

    private EquiposJpa buscarEquipo(Integer id) {
        return equiposRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Activo no encontrado"));
    }

    private CustodiosJpa buscarCustodio(Integer id) {
        return custodiosRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Custodio no encontrado"));
    }

    private CustodiosJpa buscarColaboradorActivo(Integer id) {
        CustodiosJpa custodio = buscarCustodio(id);
        if (!custodio.isEstado()) {
            throw new IllegalArgumentException("El colaborador destino no esta activo en el sistema");
        }
        return custodio;
    }

    private CategoriaEquiposJpa buscarCategoria(Integer id) {
        return categoriaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));
    }

    private MarcasJpa buscarMarca(Integer id) {
        return marcasRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));
    }

    private String normalizarCodigo(String codigo) {
        return codigo == null ? "" : codigo.trim().toUpperCase(Locale.ROOT);
    }

    private EstadoInventarioActivo resolverEstadoDevolucion(String estado) {
        if (estado == null || estado.isBlank()) {
            return EstadoInventarioActivo.EN_BODEGA;
        }
        try {
            EstadoInventarioActivo destino = EstadoInventarioActivo.valueOf(estado.trim().toUpperCase(Locale.ROOT));
            if (destino == EstadoInventarioActivo.EN_BODEGA || destino == EstadoInventarioActivo.EN_REPARACION) {
                return destino;
            }
        } catch (IllegalArgumentException ignored) {
            // Se normaliza a disponible si llega un valor no soportado desde clientes antiguos.
        }
        return EstadoInventarioActivo.EN_BODEGA;
    }

    private String unirObservacion(String actual, String nueva) {
        if (nueva == null || nueva.isBlank()) {
            return actual;
        }
        if (actual == null || actual.isBlank()) {
            return nueva;
        }
        return actual + " | " + nueva;
    }

    private String observacionBaja(BajaActivoRequestDTO request) {
        String motivo = request.getMotivo() == null ? "" : request.getMotivo().trim();
        String observacion = request.getObservacion() == null ? "" : request.getObservacion().trim();
        if (motivo.isBlank()) {
            return observacion;
        }
        if (observacion.isBlank()) {
            return "Baja: " + motivo;
        }
        return "Baja: " + motivo + " | " + observacion;
    }

    private CustodiasResponseDTO toCustodiaResponse(CustodiasJpa custodia) {
        CustodiasResponseDTO dto = new CustodiasResponseDTO();
        dto.setIdCustodiaEquipo(custodia.getIdCustodiaEquipo());
        dto.setFechaInicio(custodia.getFechaInicio());
        dto.setFechaFin(custodia.getFechaFin());
        dto.setObservacion(custodia.getObservacion());
        dto.setEstado(custodia.isEstado());
        dto.setTipoMovimiento(custodia.getTipoMovimiento());
        dto.setRutaActaPdf(custodia.getRutaActaPdf());
        dto.setRutaActaFirmada(custodia.getRutaActaFirmada());
        if (custodia.getFkEquipo() != null) {
            dto.setFkEquipo(toEquipoResponse(custodia.getFkEquipo()));
        }
        if (custodia.getFkCustodio() != null) {
            dto.setFkCustodio(toCustodioResponse(custodia.getFkCustodio()));
            dto.setIdCustodio(custodia.getFkCustodio().getIdCustodio());
        }
        return dto;
    }

    private EquiposResponseDTO toEquipoResponse(EquiposJpa equipo) {
        EquiposResponseDTO dto = new EquiposResponseDTO();
        dto.setIdEquipo(equipo.getIdEquipo());
        dto.setCodigoSap(equipo.getCodigoCresio() != null ? equipo.getCodigoCresio() : equipo.getCodigoSap());
        dto.setModelo(equipo.getModelo());
        dto.setSerial(equipo.getSerial());
        dto.setProcesador(equipo.getProcesador());
        dto.setMemoriaRamGb(equipo.getMemoriaRamGb());
        dto.setCapacidadAlmacenamientoGb(equipo.getCapacidadAlmacenamientoGb());
        dto.setLicenciaWindowsActivada(equipo.getLicenciaWindowsActivada());
        dto.setMac(equipo.getMac());
        dto.setFechaCompra(equipo.getFechaCompra());
        dto.setPrecioCompra(equipo.getPrecioCompra());
        dto.setEstadoEquipo(equipo.getEstadoEquipo());
        dto.setObservacionEquipo(equipo.getObservacionEquipo());
        dto.setEstado(equipo.isEstado());
        dto.setFechaAdquisicion(equipo.getFechaAdquisicion());
        dto.setValorActual(equipo.getValorActual());
        dto.setDescripcion(equipo.getDescripcion());
        if (equipo.getFkMarcas() != null) {
            MarcasResponseDTO marca = new MarcasResponseDTO();
            marca.setIdMarca(equipo.getFkMarcas().getIdMarca());
            marca.setNombre(equipo.getFkMarcas().getNombre());
            marca.setEstado(equipo.getFkMarcas().isEstado());
            dto.setFkMarca(marca);
        }
        if (equipo.getFkCategoria() != null) {
            CategoriaEquiposResponseDTO categoria = new CategoriaEquiposResponseDTO();
            categoria.setIdCategoria(equipo.getFkCategoria().getIdCategoria());
            categoria.setNombre(equipo.getFkCategoria().getNombre());
            categoria.setEstado(equipo.getFkCategoria().isEstado());
            dto.setFkCategoria(categoria);
        }
        return dto;
    }

    private CustodiosResponseDTO toCustodioResponse(CustodiosJpa custodio) {
        CustodiosResponseDTO dto = new CustodiosResponseDTO();
        dto.setIdCustodio(custodio.getIdCustodio());
        dto.setNombre(custodio.getNombre());
        dto.setCedula(custodio.getCedula());
        dto.setCorreo(custodio.getCorreo());
        dto.setTelefono(custodio.getTelefono());
        dto.setFechaIngreso(custodio.getFechaIngreso());
        dto.setEstado(custodio.isEstado());
        if (custodio.getFkCargo() != null) {
            CargosResponseDTO cargo = new CargosResponseDTO();
            cargo.setIdCargo(custodio.getFkCargo().getIdCargo());
            cargo.setNombre(custodio.getFkCargo().getNombre());
            cargo.setEstado(custodio.getFkCargo().isEstado());
            cargo.setFkDepartamento(toDepartamentoResponse(custodio.getFkCargo().getFkDepartamento()));
            dto.setFkCargo(cargo);
            dto.setFkDepartamento(cargo.getFkDepartamento());
        }
        if (custodio.getFkUbicacion() != null) {
            UbicacionesResponseDTO ubicacion = new UbicacionesResponseDTO();
            ubicacion.setIdUbicacion(custodio.getFkUbicacion().getIdUbicacion());
            ubicacion.setNombre(custodio.getFkUbicacion().getNombre());
            ubicacion.setAgencia(custodio.getFkUbicacion().getAgencia());
            ubicacion.setEstado(custodio.getFkUbicacion().isEstado());
            ubicacion.setCiudad(custodio.getFkUbicacion().getCiudad());
            ubicacion.setDireccion(custodio.getFkUbicacion().getDireccion());
            ubicacion.setFkDepartamento(toDepartamentoResponse(custodio.getFkUbicacion().getFkDepartamento()));
            dto.setFkUbicacion(ubicacion);
        }
        return dto;
    }

    private DepartamentosResponseDTO toDepartamentoResponse(DepartamentosJpa departamento) {
        if (departamento == null) {
            return null;
        }
        DepartamentosResponseDTO dto = new DepartamentosResponseDTO();
        dto.setIdDepartamento(departamento.getIdDepartamento());
        dto.setNombre(departamento.getNombre());
        dto.setTipo(departamento.getTipo());
        dto.setEstado(departamento.isEstado());
        return dto;
    }

    private BodegaResponseDTO toBodegaResponse(BodegaJpa bodega) {
        BodegaResponseDTO dto = new BodegaResponseDTO();
        dto.setIdBodega(bodega.getIdBodega());
        dto.setCodigo(bodega.getCodigo());
        dto.setNombre(bodega.getNombre());
        dto.setCiudad(bodega.getCiudad());
        dto.setDireccion(bodega.getDireccion());
        dto.setEstado(bodega.isEstado());
        if (bodega.getCustodioResponsable() != null) {
            dto.setCustodioResponsableId(bodega.getCustodioResponsable().getIdCustodio());
            dto.setCustodioResponsableNombre(bodega.getCustodioResponsable().getNombre());
        }
        return dto;
    }

    private ConsumibleResponseDTO toConsumibleResponse(ConsumibleJpa consumible) {
        ConsumibleResponseDTO dto = new ConsumibleResponseDTO();
        dto.setIdConsumible(consumible.getIdConsumible());
        dto.setCodigo(consumible.getCodigo());
        dto.setNombre(consumible.getNombre());
        dto.setDescripcion(consumible.getDescripcion());
        dto.setUnidadMedida(consumible.getUnidadMedida());
        dto.setEstado(consumible.isEstado());
        int total = stockRepo.findByConsumible_IdConsumible(consumible.getIdConsumible())
                .stream()
                .mapToInt(s -> s.getCantidad() == null ? 0 : s.getCantidad())
                .sum();
        dto.setStockTotal(total);
        return dto;
    }

    private OrdenCompraResponseDTO toOrdenResponse(OrdenCompraJpa orden) {
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
        dto.setDetalles(detalleRepo.findByOrdenCompra_IdOrdenCompra(orden.getIdOrdenCompra()).stream()
                .map(this::toDetalleResponse)
                .toList());
        return dto;
    }

    private OrdenCompraDetalleResponseDTO toDetalleResponse(OrdenCompraDetalleJpa detalle) {
        OrdenCompraDetalleResponseDTO dto = new OrdenCompraDetalleResponseDTO();
        dto.setIdOrdenCompraDetalle(detalle.getIdOrdenCompraDetalle());
        dto.setTipoItem(detalle.getTipoItem());
        dto.setEstado(detalle.getEstado() != null ? detalle.getEstado().name() : null);
        dto.setDescripcion(detalle.getDescripcion());
        dto.setCantidadSolicitada(detalle.getCantidadSolicitada());
        dto.setCantidadRecibida(detalle.getCantidadRecibida());
        if (detalle.getCategoria() != null) {
            dto.setCategoriaId(detalle.getCategoria().getIdCategoria());
            dto.setCategoriaNombre(detalle.getCategoria().getNombre());
        }
        if (detalle.getMarca() != null) {
            dto.setMarcaId(detalle.getMarca().getIdMarca());
            dto.setMarcaNombre(detalle.getMarca().getNombre());
        }
        if (detalle.getConsumible() != null) {
            dto.setConsumibleId(detalle.getConsumible().getIdConsumible());
            dto.setConsumibleNombre(detalle.getConsumible().getNombre());
        }
        return dto;
    }

    private ActivoInventarioResponseDTO toActivoResponse(EquiposJpa equipo) {
        ActivoInventarioResponseDTO dto = new ActivoInventarioResponseDTO();
        dto.setIdEquipo(equipo.getIdEquipo());
        dto.setCodigoCresio(equipo.getCodigoCresio());
        dto.setCodigoSap(equipo.getCodigoSap());
        dto.setModelo(equipo.getModelo());
        dto.setSerial(equipo.getSerial());
        dto.setEstadoInventario(equipo.getEstadoInventario());
        dto.setEtiquetado(equipo.getEtiquetado());
        if (equipo.getBodegaActual() != null) {
            dto.setBodegaId(equipo.getBodegaActual().getIdBodega());
            dto.setBodegaNombre(equipo.getBodegaActual().getNombre());
        }
        if (equipo.getOrdenCompra() != null) {
            dto.setOrdenCompraId(equipo.getOrdenCompra().getIdOrdenCompra());
            dto.setNumeroOc(equipo.getOrdenCompra().getNumeroOc());
        }
        return dto;
    }

    private StockConsumibleResponseDTO toStockResponse(StockConsumibleBodegaJpa stock) {
        StockConsumibleResponseDTO dto = new StockConsumibleResponseDTO();
        dto.setIdStockConsumibleBodega(stock.getIdStockConsumibleBodega());
        dto.setCantidad(stock.getCantidad());
        dto.setBodegaId(stock.getBodega().getIdBodega());
        dto.setBodegaNombre(stock.getBodega().getNombre());
        dto.setConsumibleId(stock.getConsumible().getIdConsumible());
        dto.setConsumibleCodigo(stock.getConsumible().getCodigo());
        dto.setConsumibleNombre(stock.getConsumible().getNombre());
        return dto;
    }

    private MovimientoInventarioResponseDTO toMovimientoResponse(MovimientoInventarioJpa movimiento) {
        MovimientoInventarioResponseDTO dto = new MovimientoInventarioResponseDTO();
        dto.setIdMovimientoInventario(movimiento.getIdMovimientoInventario());
        dto.setTipoMovimiento(movimiento.getTipoMovimiento());
        dto.setFechaMovimiento(movimiento.getFechaMovimiento());
        dto.setCantidad(movimiento.getCantidad());
        dto.setEstadoAnterior(movimiento.getEstadoAnterior());
        dto.setEstadoNuevo(movimiento.getEstadoNuevo());
        dto.setObservacion(movimiento.getObservacion());
        if (movimiento.getEquipo() != null) {
            dto.setEquipoId(movimiento.getEquipo().getIdEquipo());
            dto.setEquipoCodigo(movimiento.getEquipo().getCodigoCresio() != null
                    ? movimiento.getEquipo().getCodigoCresio()
                    : movimiento.getEquipo().getCodigoSap());
        }
        if (movimiento.getConsumible() != null) {
            dto.setConsumibleId(movimiento.getConsumible().getIdConsumible());
            dto.setConsumibleNombre(movimiento.getConsumible().getNombre());
        }
        if (movimiento.getBodegaOrigen() != null) {
            dto.setBodegaOrigenId(movimiento.getBodegaOrigen().getIdBodega());
            dto.setBodegaOrigenNombre(movimiento.getBodegaOrigen().getNombre());
        }
        if (movimiento.getBodegaDestino() != null) {
            dto.setBodegaDestinoId(movimiento.getBodegaDestino().getIdBodega());
            dto.setBodegaDestinoNombre(movimiento.getBodegaDestino().getNombre());
        }
        if (movimiento.getCustodio() != null) {
            dto.setCustodioId(movimiento.getCustodio().getIdCustodio());
            dto.setCustodioNombre(movimiento.getCustodio().getNombre());
        }
        dto.setCondicion(movimiento.getCondicion());
        dto.setRealizadoPor(movimiento.getRealizadoPor());
        dto.setMotivo(movimiento.getMotivo());
        dto.setFechaEfectiva(movimiento.getFechaEfectiva());
        return dto;
    }
}
