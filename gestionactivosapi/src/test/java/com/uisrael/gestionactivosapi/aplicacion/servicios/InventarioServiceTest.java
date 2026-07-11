package com.uisrael.gestionactivosapi.aplicacion.servicios;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoInventarioActivo;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompra;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoItemInventario;
import com.uisrael.gestionactivosapi.dominio.excepciones.CantidadExcedidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.CantidadInvalidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecepcionNoPermitidaException;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.BodegaJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CargosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ConsumibleJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiasJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.DepartamentosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MovimientoInventarioJpa;
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
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.AsignacionLoteRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.AsignacionConsumibleRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.BodegaRequestDTO;
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
    private ICustodiasJpaRepositorio custodiasRepo;
    private ICustodiosJpaRepositorio custodiosRepo;
    private IMovimientoInventarioJpaRepositorio movimientoRepo;
    private InventarioService service;

    @BeforeEach
    void setUp() {
        bodegaRepo = mock(IBodegaJpaRepositorio.class);
        consumibleRepo = mock(IConsumibleJpaRepositorio.class);
        ordenRepo = mock(IOrdenCompraJpaRepositorio.class);
        detalleRepo = mock(IOrdenCompraDetalleJpaRepositorio.class);
        equiposRepo = mock(IEquiposJpaRepositorio.class);
        custodiasRepo = mock(ICustodiasJpaRepositorio.class);
        custodiosRepo = mock(ICustodiosJpaRepositorio.class);
        movimientoRepo = mock(IMovimientoInventarioJpaRepositorio.class);
        service = new InventarioService(
                bodegaRepo,
                consumibleRepo,
                ordenRepo,
                detalleRepo,
                mock(IStockConsumibleBodegaJpaRepositorio.class),
                movimientoRepo,
                equiposRepo,
                custodiasRepo,
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
    void asignarActivosLote_devuelveCustodiaCreadaYRegistraMovimiento() {
        BodegaJpa bodega = bodega(1, true);
        EquiposJpa equipo = new EquiposJpa();
        equipo.setIdEquipo(8);
        equipo.setCodigoCresio("CR-LAP-001");
        equipo.setEstadoInventario(EstadoInventarioActivo.EN_BODEGA.name());
        equipo.setEtiquetado(true);
        equipo.setBodegaActual(bodega);
        CustodiosJpa custodio = new CustodiosJpa();
        custodio.setIdCustodio(4);
        custodio.setNombre("Usuario Prueba");
        custodio.setEstado(true);
        when(equiposRepo.findById(8)).thenReturn(Optional.of(equipo));
        when(custodiosRepo.findById(4)).thenReturn(Optional.of(custodio));
        when(bodegaRepo.findById(1)).thenReturn(Optional.of(bodega));
        when(custodiasRepo.save(any())).thenAnswer(invocation -> {
            CustodiasJpa custodia = invocation.getArgument(0);
            custodia.setIdCustodiaEquipo(99);
            return custodia;
        });
        when(equiposRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AsignacionLoteRequestDTO request = new AsignacionLoteRequestDTO();
        request.setEquipoIds(List.of(8));
        request.setCustodioId(4);

        var response = service.asignarActivosLote(request);

        assertThat(response.getActivos()).hasSize(1);
        assertThat(response.getCustodias()).hasSize(1);
        assertThat(response.getActivos().get(0).getEstadoInventario()).isEqualTo("ASIGNADO");
        assertThat(response.getCustodias().get(0).getIdCustodiaEquipo()).isEqualTo(99);
        assertThat(response.getCustodias().get(0).getFkCustodio().getIdCustodio()).isEqualTo(4);
        verify(movimientoRepo).save(any(MovimientoInventarioJpa.class));
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

    @Test
    void registrarRecepcionStock_rechazaOrdenNoRecibible() {
        for (EstadoOrdenCompra estado : List.of(EstadoOrdenCompra.BORRADOR,
                EstadoOrdenCompra.RECIBIDA, EstadoOrdenCompra.CANCELADA)) {
            OrdenCompraJpa orden = orden(1);
            orden.setEstado(estado);
            when(ordenRepo.findById(1)).thenReturn(Optional.of(orden));

            RegistrarRecepcionStockRequestDTO request = new RegistrarRecepcionStockRequestDTO();
            request.setIdBodegaDestino(3);
            request.setCantidad(1);

            assertThatThrownBy(() -> service.registrarRecepcionStockPorDetalle(1, 2, request))
                    .isInstanceOf(RecepcionNoPermitidaException.class);
        }
    }

    @Test
    void registrarRecepcionStock_rechazaDetalleNoRecibible() {
        for (EstadoOrdenCompraDetalle estado : List.of(EstadoOrdenCompraDetalle.COMPLETO,
                EstadoOrdenCompraDetalle.CANCELADO)) {
            OrdenCompraJpa orden = orden(1);
            OrdenCompraDetalleJpa detalle = detalle(2, orden, TipoItemInventario.STOCK);
            detalle.setEstado(estado);
            when(ordenRepo.findById(1)).thenReturn(Optional.of(orden));
            when(detalleRepo.findById(2)).thenReturn(Optional.of(detalle));

            RegistrarRecepcionStockRequestDTO request = new RegistrarRecepcionStockRequestDTO();
            request.setIdBodegaDestino(3);
            request.setCantidad(1);

            assertThatThrownBy(() -> service.registrarRecepcionStockPorDetalle(1, 2, request))
                    .isInstanceOf(RecepcionNoPermitidaException.class);
        }
    }

    @Test
    void registrarRecepcionStock_rechazaCantidadInvalidaOExcedida() {
        OrdenCompraJpa orden = orden(1);
        OrdenCompraDetalleJpa detalle = detalle(2, orden, TipoItemInventario.STOCK);
        detalle.setCantidadRecibida(3);
        detalle.setEstado(EstadoOrdenCompraDetalle.PARCIAL);
        when(ordenRepo.findById(1)).thenReturn(Optional.of(orden));
        when(detalleRepo.findById(2)).thenReturn(Optional.of(detalle));

        RegistrarRecepcionStockRequestDTO cero = new RegistrarRecepcionStockRequestDTO();
        cero.setIdBodegaDestino(3);
        cero.setCantidad(0);
        assertThatThrownBy(() -> service.registrarRecepcionStockPorDetalle(1, 2, cero))
                .isInstanceOf(CantidadInvalidaException.class);

        RegistrarRecepcionStockRequestDTO negativa = new RegistrarRecepcionStockRequestDTO();
        negativa.setIdBodegaDestino(3);
        negativa.setCantidad(-1);
        assertThatThrownBy(() -> service.registrarRecepcionStockPorDetalle(1, 2, negativa))
                .isInstanceOf(CantidadInvalidaException.class);

        RegistrarRecepcionStockRequestDTO excede = new RegistrarRecepcionStockRequestDTO();
        excede.setIdBodegaDestino(3);
        excede.setCantidad(3);
        assertThatThrownBy(() -> service.registrarRecepcionStockPorDetalle(1, 2, excede))
                .isInstanceOf(CantidadExcedidaException.class);
    }

    @Test
    void registrarRecepcionActivo_rechazaOrdenCancelada() {
        OrdenCompraJpa orden = orden(1);
        orden.setEstado(EstadoOrdenCompra.CANCELADA);
        when(ordenRepo.findById(1)).thenReturn(Optional.of(orden));

        RegistrarRecepcionActivoRequestDTO request = new RegistrarRecepcionActivoRequestDTO();
        request.setIdBodegaDestino(3);

        assertThatThrownBy(() -> service.registrarRecepcionActivoPorDetalle(1, 2, request))
                .isInstanceOf(RecepcionNoPermitidaException.class);
    }

    @Test
    void registrarRecepcionActivo_rechazaDetalleCompleto() {
        OrdenCompraJpa orden = orden(1);
        OrdenCompraDetalleJpa detalle = detalle(2, orden, TipoItemInventario.ACTIVO);
        detalle.setCantidadRecibida(5);
        detalle.setEstado(EstadoOrdenCompraDetalle.COMPLETO);
        when(ordenRepo.findById(1)).thenReturn(Optional.of(orden));
        when(detalleRepo.findById(2)).thenReturn(Optional.of(detalle));

        RegistrarRecepcionActivoRequestDTO request = new RegistrarRecepcionActivoRequestDTO();
        request.setIdBodegaDestino(3);

        assertThatThrownBy(() -> service.registrarRecepcionActivoPorDetalle(1, 2, request))
                .isInstanceOf(RecepcionNoPermitidaException.class);
    }

    @Test
    void crearBodega_rechazaSinCustodioResponsable() {
        BodegaRequestDTO request = bodegaRequest("BOD-01", null);

        assertThatThrownBy(() -> service.crearBodega(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("custodio responsable");
    }

    @Test
    void crearBodega_rechazaCustodioInactivo() {
        CustodiosJpa custodio = custodioConDepartamento(1, false, "TECNOLOGÍAS E INNOVACIÓN");
        when(custodiosRepo.findById(1)).thenReturn(Optional.of(custodio));
        BodegaRequestDTO request = bodegaRequest("BOD-02", 1);

        assertThatThrownBy(() -> service.crearBodega(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("activo");
    }

    @Test
    void crearBodega_rechazaCustodioFueraDeDepartamentoTic() {
        CustodiosJpa custodio = custodioConDepartamento(2, true, "OFICINA DE VALOR");
        when(custodiosRepo.findById(2)).thenReturn(Optional.of(custodio));
        BodegaRequestDTO request = bodegaRequest("BOD-03", 2);

        assertThatThrownBy(() -> service.crearBodega(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TECNOLOGÍAS E INNOVACIÓN");
    }

    @Test
    void crearBodega_aceptaDepartamentoDelCatalogoIgnorandoTildesYMayusculas() {
        CustodiosJpa custodio = custodioConDepartamento(3, true, "tecnologias e innovacion");
        when(custodiosRepo.findById(3)).thenReturn(Optional.of(custodio));
        when(bodegaRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        BodegaRequestDTO request = bodegaRequest("BOD-04", 3);

        service.crearBodega(request);

        verify(bodegaRepo).save(any());
    }

    @Test
    void crearBodega_aceptaDepartamentoDelDirectorioCuandoNoHayCatalogo() {
        CustodiosJpa custodio = new CustodiosJpa();
        custodio.setIdCustodio(4);
        custodio.setEstado(true);
        custodio.setDepartamentoDirectorio("Tecnologías e Innovación");
        when(custodiosRepo.findById(4)).thenReturn(Optional.of(custodio));
        when(bodegaRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        BodegaRequestDTO request = bodegaRequest("BOD-05", 4);

        service.crearBodega(request);

        verify(bodegaRepo).save(any());
    }

    private BodegaRequestDTO bodegaRequest(String codigo, Integer custodioResponsableId) {
        BodegaRequestDTO request = new BodegaRequestDTO();
        request.setCodigo(codigo);
        request.setNombre("Bodega TIC");
        request.setCustodioResponsableId(custodioResponsableId);
        return request;
    }

    private CustodiosJpa custodioConDepartamento(int id, boolean estado, String nombreDepartamento) {
        DepartamentosJpa departamento = new DepartamentosJpa();
        departamento.setNombre(nombreDepartamento);
        CargosJpa cargo = new CargosJpa();
        cargo.setFkDepartamento(departamento);
        CustodiosJpa custodio = new CustodiosJpa();
        custodio.setIdCustodio(id);
        custodio.setEstado(estado);
        custodio.setFkCargo(cargo);
        return custodio;
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
