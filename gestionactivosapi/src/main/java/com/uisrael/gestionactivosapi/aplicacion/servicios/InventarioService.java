package com.uisrael.gestionactivosapi.aplicacion.servicios;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

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
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.AsignacionConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.BajaActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.BodegaRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.ConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.DevolucionActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.DevolucionConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.OrdenCompraDetalleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.OrdenCompraRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RecepcionActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RecepcionConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.TrasladoActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.TrasladoConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.ConsumibleResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.MovimientoInventarioResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.OrdenCompraDetalleResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.StockConsumibleResponseDTO;

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
            IMantenimientosJpaRepositorio mantenimientosRepo) {
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
        consumible.setNombre(request.getNombre());
        consumible.setDescripcion(request.getDescripcion());
        consumible.setUnidadMedida(request.getUnidadMedida());
        consumible.setEstado(request.getEstado() == null || request.getEstado());
        return toConsumibleResponse(consumibleRepo.save(consumible));
    }

    public List<OrdenCompraResponseDTO> listarOrdenesCompra() {
        return ordenRepo.findAll().stream().map(this::toOrdenResponse).toList();
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
    public OrdenCompraResponseDTO crearOrdenCompra(OrdenCompraRequestDTO request) {
        String numeroOc = request.getNumeroOc().trim().toUpperCase(Locale.ROOT);
        if (ordenRepo.existsByNumeroOcIgnoreCase(numeroOc)) {
            throw new IllegalArgumentException("Ya existe una orden de compra con numero " + numeroOc);
        }
        BodegaJpa bodega = buscarBodega(request.getBodegaDestinoId());
        OrdenCompraJpa orden = new OrdenCompraJpa();
        orden.setNumeroOc(numeroOc);
        orden.setProveedor(request.getProveedor());
        orden.setFechaEmision(request.getFechaEmision() == null ? LocalDate.now() : request.getFechaEmision());
        orden.setObservacion(request.getObservacion());
        orden.setEstado(EstadoOrdenCompra.EMITIDA);
        orden.setBodegaDestino(bodega);
        OrdenCompraJpa guardada = ordenRepo.save(orden);

        for (OrdenCompraDetalleRequestDTO detalleRequest : request.getDetalles()) {
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
                detalle.setConsumible(buscarConsumible(detalleRequest.getConsumibleId()));
            }
            detalleRepo.save(detalle);
        }
        return toOrdenResponse(guardada);
    }

    @Transactional
    public ActivoInventarioResponseDTO recibirActivo(RecepcionActivoRequestDTO request) {
        OrdenCompraJpa orden = buscarOrden(request.getOrdenCompraId());
        BodegaJpa bodega = buscarBodega(request.getBodegaId());
        CategoriaEquiposJpa categoria = buscarCategoria(request.getCategoriaId());
        MarcasJpa marca = buscarMarca(request.getMarcaId());

        if (equiposRepo.existsBySerialIgnoreCase(request.getSerial())) {
            throw new IllegalArgumentException("Ya existe un activo con serial " + request.getSerial());
        }
        if (request.getMac() != null && !request.getMac().isBlank()
                && equiposRepo.existsByMacIgnoreCase(request.getMac())) {
            throw new IllegalArgumentException("Ya existe un activo con MAC " + request.getMac());
        }

        validarRecepcionActivoNoExcedeOrden(orden, categoria, marca);
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
        equipo.setEtiquetado(Boolean.TRUE.equals(request.getEtiquetado()));
        EquiposJpa guardado = equiposRepo.save(equipo);

        incrementarDetalleActivo(orden, categoria, marca);
        marcarOrdenRecibidaParcial(orden);
        registrarMovimiento(TipoMovimientoInventario.INGRESO_ACTIVO, guardado, null, 1, null,
                bodega, null, orden, null, EstadoInventarioActivo.EN_BODEGA.name(), request.getObservacion());
        return toActivoResponse(guardado);
    }

    @Transactional
    public StockConsumibleResponseDTO recibirConsumible(RecepcionConsumibleRequestDTO request) {
        OrdenCompraJpa orden = buscarOrden(request.getOrdenCompraId());
        BodegaJpa bodega = buscarBodega(request.getBodegaId());
        ConsumibleJpa consumible = buscarConsumible(request.getConsumibleId());

        validarRecepcionConsumibleNoExcedeOrden(orden, consumible, request.getCantidad());
        StockConsumibleBodegaJpa stock = stockRepo
                .findByBodega_IdBodegaAndConsumible_IdConsumible(bodega.getIdBodega(), consumible.getIdConsumible())
                .orElseGet(() -> {
                    StockConsumibleBodegaJpa nuevo = new StockConsumibleBodegaJpa();
                    nuevo.setBodega(bodega);
                    nuevo.setConsumible(consumible);
                    nuevo.setCantidad(0);
                    return nuevo;
                });
        stock.setCantidad(stock.getCantidad() + request.getCantidad());
        StockConsumibleBodegaJpa guardado = stockRepo.save(stock);

        incrementarDetalleConsumible(orden, consumible, request.getCantidad());
        marcarOrdenRecibidaParcial(orden);
        registrarMovimiento(TipoMovimientoInventario.INGRESO_CONSUMIBLE, null, consumible, request.getCantidad(),
                null, bodega, null, orden, null, null, request.getObservacion());
        return toStockResponse(guardado);
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

    @Transactional
    public ActivoInventarioResponseDTO asignarActivo(AsignacionActivoRequestDTO request) {
        EquiposJpa equipo = buscarEquipo(request.getEquipoId());
        if (!EstadoInventarioActivo.EN_BODEGA.name().equals(equipo.getEstadoInventario())) {
            throw new IllegalArgumentException("El activo no esta disponible en bodega");
        }
        if (custodiasRepo.existsByFkEquipo_IdEquipoAndEstadoTrue(equipo.getIdEquipo())) {
            throw new IllegalArgumentException("El activo ya tiene una custodia activa");
        }
        if (!Boolean.TRUE.equals(equipo.getEtiquetado())) {
            throw new IllegalArgumentException("El activo debe estar etiquetado antes de asignarse");
        }

        CustodiosJpa custodio = buscarCustodio(request.getCustodioId());
        BodegaJpa bodegaOrigen = equipo.getBodegaActual();
        String estadoAnterior = equipo.getEstadoInventario();

        CustodiasJpa custodia = new CustodiasJpa();
        custodia.setFkEquipo(equipo);
        custodia.setFkCustodio(custodio);
        custodia.setFechaInicio(request.getFechaInicio() == null ? LocalDate.now() : request.getFechaInicio());
        custodia.setObservacion(request.getObservacion());
        custodia.setEstado(true);
        custodia.setTipoMovimiento("ASIGNACION");
        custodiasRepo.save(custodia);

        equipo.setEstadoInventario(EstadoInventarioActivo.ASIGNADO.name());
        equipo.setBodegaActual(null);
        EquiposJpa guardado = equiposRepo.save(equipo);

        registrarMovimiento(TipoMovimientoInventario.ASIGNACION_ACTIVO, guardado, null, 1,
                bodegaOrigen, null, custodio, guardado.getOrdenCompra(), estadoAnterior,
                EstadoInventarioActivo.ASIGNADO.name(), request.getObservacion());
        return toActivoResponse(guardado);
    }

    @Transactional
    public StockConsumibleResponseDTO asignarConsumible(AsignacionConsumibleRequestDTO request) {
        BodegaJpa bodega = buscarBodega(request.getBodegaId());
        ConsumibleJpa consumible = buscarConsumible(request.getConsumibleId());
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
                bodega, null, custodio, null, null, null, request.getObservacion());
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
                request.getObservacion());
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
                null, bodega, custodio, null, null, null, request.getObservacion());
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

        equipo.setBodegaActual(destino);
        EquiposJpa guardado = equiposRepo.save(equipo);

        registrarMovimiento(TipoMovimientoInventario.TRASLADO, guardado, null, 1,
                origen, destino, null, guardado.getOrdenCompra(), EstadoInventarioActivo.EN_BODEGA.name(),
                EstadoInventarioActivo.EN_BODEGA.name(), request.getObservacion());
        return toActivoResponse(guardado);
    }

    @Transactional
    public StockConsumibleResponseDTO trasladarConsumible(TrasladoConsumibleRequestDTO request) {
        BodegaJpa origen = buscarBodega(request.getBodegaOrigenId());
        BodegaJpa destino = buscarBodega(request.getBodegaDestinoId());
        if (origen.getIdBodega().equals(destino.getIdBodega())) {
            throw new IllegalArgumentException("La bodega destino debe ser diferente a la bodega origen");
        }
        ConsumibleJpa consumible = buscarConsumible(request.getConsumibleId());

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
                origen, destino, null, null, null, null, request.getObservacion());
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
                EstadoInventarioActivo.DADO_DE_BAJA.name(), observacionBaja(request));
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

    private void incrementarDetalleActivo(OrdenCompraJpa orden, CategoriaEquiposJpa categoria, MarcasJpa marca) {
        detalleRepo.findByOrdenCompra_IdOrdenCompra(orden.getIdOrdenCompra()).stream()
                .filter(d -> d.getTipoItem() == TipoItemInventario.ACTIVO)
                .filter(d -> d.getCategoria() == null || d.getCategoria().getIdCategoria() == categoria.getIdCategoria())
                .filter(d -> d.getMarca() == null || d.getMarca().getIdMarca() == marca.getIdMarca())
                .findFirst()
                .ifPresent(d -> {
                    d.setCantidadRecibida((d.getCantidadRecibida() == null ? 0 : d.getCantidadRecibida()) + 1);
                    detalleRepo.save(d);
                });
    }

    private void validarRecepcionActivoNoExcedeOrden(OrdenCompraJpa orden, CategoriaEquiposJpa categoria, MarcasJpa marca) {
        OrdenCompraDetalleJpa detalle = detalleRepo.findByOrdenCompra_IdOrdenCompra(orden.getIdOrdenCompra()).stream()
                .filter(d -> d.getTipoItem() == TipoItemInventario.ACTIVO)
                .filter(d -> d.getCategoria() == null || d.getCategoria().getIdCategoria() == categoria.getIdCategoria())
                .filter(d -> d.getMarca() == null || d.getMarca().getIdMarca() == marca.getIdMarca())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La orden de compra no contempla este activo"));
        int recibido = detalle.getCantidadRecibida() == null ? 0 : detalle.getCantidadRecibida();
        if (recibido + 1 > detalle.getCantidadSolicitada()) {
            throw new IllegalArgumentException("La recepcion excede la cantidad solicitada para este activo");
        }
    }

    private void incrementarDetalleConsumible(OrdenCompraJpa orden, ConsumibleJpa consumible, Integer cantidad) {
        detalleRepo.findByOrdenCompra_IdOrdenCompra(orden.getIdOrdenCompra()).stream()
                .filter(d -> d.getTipoItem() == TipoItemInventario.CONSUMIBLE)
                .filter(d -> d.getConsumible() == null || d.getConsumible().getIdConsumible().equals(consumible.getIdConsumible()))
                .findFirst()
                .ifPresent(d -> {
                    d.setCantidadRecibida((d.getCantidadRecibida() == null ? 0 : d.getCantidadRecibida()) + cantidad);
                    detalleRepo.save(d);
                });
    }

    private void validarRecepcionConsumibleNoExcedeOrden(OrdenCompraJpa orden, ConsumibleJpa consumible, Integer cantidad) {
        OrdenCompraDetalleJpa detalle = detalleRepo.findByOrdenCompra_IdOrdenCompra(orden.getIdOrdenCompra()).stream()
                .filter(d -> d.getTipoItem() == TipoItemInventario.CONSUMIBLE)
                .filter(d -> d.getConsumible() == null || d.getConsumible().getIdConsumible().equals(consumible.getIdConsumible()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La orden de compra no contempla este consumible"));
        int recibido = detalle.getCantidadRecibida() == null ? 0 : detalle.getCantidadRecibida();
        if (recibido + cantidad > detalle.getCantidadSolicitada()) {
            throw new IllegalArgumentException("La recepcion excede la cantidad solicitada para este consumible");
        }
    }

    private void validarOrdenCompleta(OrdenCompraJpa orden) {
        List<OrdenCompraDetalleJpa> detalles = detalleRepo.findByOrdenCompra_IdOrdenCompra(orden.getIdOrdenCompra());
        if (detalles.isEmpty()) {
            throw new IllegalArgumentException("La orden de compra no tiene detalles");
        }
        boolean incompleta = detalles.stream().anyMatch(d ->
                (d.getCantidadRecibida() == null ? 0 : d.getCantidadRecibida()) < d.getCantidadSolicitada());
        if (incompleta) {
            throw new IllegalArgumentException("No se puede cerrar la orden: aun existen items pendientes de recepcion");
        }
    }

    private void marcarOrdenRecibidaParcial(OrdenCompraJpa orden) {
        orden.setFechaRecepcion(LocalDate.now());
        if (orden.getEstado() != EstadoOrdenCompra.RECIBIDA) {
            orden.setEstado(EstadoOrdenCompra.RECIBIDA_PARCIAL);
        }
        ordenRepo.save(orden);
    }

    private void registrarMovimiento(TipoMovimientoInventario tipo, EquiposJpa equipo, ConsumibleJpa consumible,
            Integer cantidad, BodegaJpa origen, BodegaJpa destino, CustodiosJpa custodio, OrdenCompraJpa orden,
            String estadoAnterior, String estadoNuevo, String observacion) {
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
        movimientoRepo.save(movimiento);
    }

    private BodegaJpa buscarBodega(Integer id) {
        return bodegaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada"));
    }

    private ConsumibleJpa buscarConsumible(Integer id) {
        return consumibleRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Consumible no encontrado"));
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
        return dto;
    }
}
