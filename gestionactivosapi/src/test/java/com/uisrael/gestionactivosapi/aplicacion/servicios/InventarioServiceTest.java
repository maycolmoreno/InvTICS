package com.uisrael.gestionactivosapi.aplicacion.servicios;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoInventarioActivo;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoItemInventario;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.BodegaJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ConsumibleJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraDetalleJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraJpa;
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
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRecepcionLoteJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IStockConsumibleBodegaJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.AsignacionActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.AsignacionConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.OrdenCompraDetalleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.OrdenCompraRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RegistrarRecepcionActivoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RegistrarRecepcionStockRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RetornarReparacionRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.TrasladoConsumibleRequestDTO;

class InventarioServiceTest {

    private IBodegaJpaRepositorio bodegaRepo;
    private IConsumibleJpaRepositorio consumibleRepo;
    private IOrdenCompraJpaRepositorio ordenRepo;
    private IOrdenCompraDetalleJpaRepositorio detalleRepo;
    private IEquiposJpaRepositorio equiposRepo;
    private ICustodiosJpaRepositorio custodiosRepo;
    private InventarioService service;

    @BeforeEach
    void setUp() {
        bodegaRepo = mock(IBodegaJpaRepositorio.class);
        consumibleRepo = mock(IConsumibleJpaRepositorio.class);
        ordenRepo = mock(IOrdenCompraJpaRepositorio.class);
        detalleRepo = mock(IOrdenCompraDetalleJpaRepositorio.class);
        equiposRepo = mock(IEquiposJpaRepositorio.class);
        custodiosRepo = mock(ICustodiosJpaRepositorio.class);
        service = new InventarioService(
                bodegaRepo,
                consumibleRepo,
                ordenRepo,
                detalleRepo,
                mock(IStockConsumibleBodegaJpaRepositorio.class),
                mock(IMovimientoInventarioJpaRepositorio.class),
                equiposRepo,
                mock(ICustodiasJpaRepositorio.class),
                mock(ICategoriaEquiposJpaRepositorio.class),
                mock(IMarcasJpaRepositorio.class),
                custodiosRepo,
                mock(IMantenimientosJpaRepositorio.class),
                mock(IRecepcionLoteJpaRepositorio.class));
    }

    @Test
    void crearOrdenCompra_rechazaConsumibleInactivo() {
        BodegaJpa bodega = bodega(1, true);
        ConsumibleJpa consumible = consumible(9, false);
        OrdenCompraJpa ordenGuardada = new OrdenCompraJpa();
        ordenGuardada.setIdOrdenCompra(1);

        when(ordenRepo.existsByNumeroOcIgnoreCase("OC-001")).thenReturn(false);
        when(bodegaRepo.findById(1)).thenReturn(Optional.of(bodega));
        when(ordenRepo.save(any())).thenReturn(ordenGuardada);
        when(consumibleRepo.findById(9)).thenReturn(Optional.of(consumible));

        OrdenCompraDetalleRequestDTO detalle = new OrdenCompraDetalleRequestDTO();
        detalle.setTipoItem(TipoItemInventario.STOCK);
        detalle.setDescripcion("Toner");
        detalle.setCantidadSolicitada(1);
        detalle.setConsumibleId(9);

        OrdenCompraRequestDTO request = new OrdenCompraRequestDTO();
        request.setNumeroOc("OC-001");
        request.setBodegaDestinoId(1);
        request.setDetalles(List.of(detalle));

        assertThatThrownBy(() -> service.crearOrdenCompra(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No se puede operar con un consumible inactivo");
    }

    @Test
    void asignarConsumible_rechazaConsumibleInactivo() {
        when(bodegaRepo.findById(1)).thenReturn(Optional.of(bodega(1, true)));
        when(consumibleRepo.findById(9)).thenReturn(Optional.of(consumible(9, false)));

        AsignacionConsumibleRequestDTO request = new AsignacionConsumibleRequestDTO();
        request.setBodegaId(1);
        request.setConsumibleId(9);
        request.setCustodioId(3);
        request.setCantidad(1);

        assertThatThrownBy(() -> service.asignarConsumible(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No se puede operar con un consumible inactivo");
    }

    @Test
    void trasladarConsumible_rechazaBodegaDestinoInactiva() {
        when(bodegaRepo.findById(1)).thenReturn(Optional.of(bodega(1, true)));
        when(bodegaRepo.findById(2)).thenReturn(Optional.of(bodega(2, false)));

        TrasladoConsumibleRequestDTO request = new TrasladoConsumibleRequestDTO();
        request.setBodegaOrigenId(1);
        request.setBodegaDestinoId(2);
        request.setConsumibleId(9);
        request.setCantidad(1);

        assertThatThrownBy(() -> service.trasladarConsumible(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La bodega seleccionada se encuentra inactiva o fuera de servicio");
    }

    @Test
    void registrarRecepcionStock_rechazaBodegaInactiva() {
        OrdenCompraJpa orden = orden(1);
        OrdenCompraDetalleJpa detalle = detalle(2, orden, TipoItemInventario.STOCK);
        when(ordenRepo.findById(1)).thenReturn(Optional.of(orden));
        when(detalleRepo.findById(2)).thenReturn(Optional.of(detalle));
        when(bodegaRepo.findById(3)).thenReturn(Optional.of(bodega(3, false)));

        RegistrarRecepcionStockRequestDTO request = new RegistrarRecepcionStockRequestDTO();
        request.setIdBodegaDestino(3);
        request.setCantidad(1);

        assertThatThrownBy(() -> service.registrarRecepcionStockPorDetalle(1, 2, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La bodega seleccionada se encuentra inactiva o fuera de servicio");
    }

    @Test
    void registrarRecepcionActivo_rechazaBodegaInactiva() {
        OrdenCompraJpa orden = orden(1);
        OrdenCompraDetalleJpa detalle = detalle(2, orden, TipoItemInventario.ACTIVO);
        when(ordenRepo.findById(1)).thenReturn(Optional.of(orden));
        when(detalleRepo.findById(2)).thenReturn(Optional.of(detalle));
        when(bodegaRepo.findById(3)).thenReturn(Optional.of(bodega(3, false)));

        RegistrarRecepcionActivoRequestDTO request = new RegistrarRecepcionActivoRequestDTO();
        request.setIdBodegaDestino(3);

        assertThatThrownBy(() -> service.registrarRecepcionActivoPorDetalle(1, 2, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La bodega seleccionada se encuentra inactiva o fuera de servicio");
    }

    @Test
    void retornarDeReparacion_rechazaBodegaDestinoInactiva() {
        EquiposJpa equipo = new EquiposJpa();
        equipo.setIdEquipo(8);
        equipo.setEstadoInventario(EstadoInventarioActivo.EN_REPARACION.name());
        when(equiposRepo.findById(8)).thenReturn(Optional.of(equipo));
        when(bodegaRepo.findById(3)).thenReturn(Optional.of(bodega(3, false)));

        RetornarReparacionRequestDTO request = new RetornarReparacionRequestDTO();
        request.setEquipoId(8);
        request.setBodegaDestinoId(3);

        assertThatThrownBy(() -> service.retornarDeReparacion(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La bodega seleccionada se encuentra inactiva o fuera de servicio");
    }

    @Test
    void asignarActivo_rechazaCustodioInactivo() {
        BodegaJpa bodega = bodega(1, true);
        EquiposJpa equipo = new EquiposJpa();
        equipo.setIdEquipo(8);
        equipo.setEstadoInventario(EstadoInventarioActivo.EN_BODEGA.name());
        equipo.setEtiquetado(true);
        equipo.setBodegaActual(bodega);
        CustodiosJpa custodio = new CustodiosJpa();
        custodio.setIdCustodio(4);
        custodio.setEstado(false);
        when(equiposRepo.findById(8)).thenReturn(Optional.of(equipo));
        when(bodegaRepo.findById(1)).thenReturn(Optional.of(bodega));
        when(custodiosRepo.findById(4)).thenReturn(Optional.of(custodio));

        AsignacionActivoRequestDTO request = new AsignacionActivoRequestDTO();
        request.setEquipoId(8);
        request.setCustodioId(4);

        assertThatThrownBy(() -> service.asignarActivo(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El colaborador destino no esta activo en el sistema");
    }

    @Test
    void listarActivosEnBodega_exponeEstadoDeEtiqueta() {
        EquiposJpa equipo = new EquiposJpa();
        equipo.setIdEquipo(8);
        equipo.setEstadoInventario(EstadoInventarioActivo.EN_BODEGA.name());
        equipo.setEtiquetado(true);
        when(equiposRepo.findByEstadoInventarioAndEstadoTrue(EstadoInventarioActivo.EN_BODEGA.name()))
                .thenReturn(List.of(equipo));

        var activos = service.listarActivosEnBodega();

        assertThat(activos).hasSize(1);
        assertThat(activos.get(0).getEtiquetado()).isTrue();
    }

    private BodegaJpa bodega(Integer id, boolean estado) {
        BodegaJpa bodega = new BodegaJpa();
        bodega.setIdBodega(id);
        bodega.setEstado(estado);
        return bodega;
    }

    private ConsumibleJpa consumible(Integer id, boolean estado) {
        ConsumibleJpa consumible = new ConsumibleJpa();
        consumible.setIdConsumible(id);
        consumible.setEstado(estado);
        return consumible;
    }

    private OrdenCompraJpa orden(Integer id) {
        OrdenCompraJpa orden = new OrdenCompraJpa();
        orden.setIdOrdenCompra(id);
        return orden;
    }

    private OrdenCompraDetalleJpa detalle(Integer id, OrdenCompraJpa orden, TipoItemInventario tipo) {
        OrdenCompraDetalleJpa detalle = new OrdenCompraDetalleJpa();
        detalle.setIdOrdenCompraDetalle(id);
        detalle.setOrdenCompra(orden);
        detalle.setTipoItem(tipo);
        detalle.setCantidadSolicitada(5);
        detalle.setCantidadRecibida(0);
        detalle.setEstado(EstadoOrdenCompraDetalle.PENDIENTE);
        return detalle;
    }
}
