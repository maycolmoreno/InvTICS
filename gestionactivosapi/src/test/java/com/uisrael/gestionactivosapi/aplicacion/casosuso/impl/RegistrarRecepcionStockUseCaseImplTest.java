package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos.RegistrarRecepcionStockCommand;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompra;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoItemInventario;
import com.uisrael.gestionactivosapi.dominio.excepciones.CantidadExcedidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.CantidadInvalidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecepcionNoPermitidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.BodegaJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ConsumibleJpa;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistrarRecepcionStockUseCaseImpl")
class RegistrarRecepcionStockUseCaseImplTest {

    @Mock private IOrdenCompraJpaRepositorio ordenCompraRepo;
    @Mock private IOrdenCompraDetalleJpaRepositorio detalleRepo;
    @Mock private IBodegaJpaRepositorio bodegaRepo;
    @Mock private IStockConsumibleBodegaJpaRepositorio stockRepo;
    @Mock private IMovimientoInventarioJpaRepositorio movimientoRepo;
    @Mock private IRecepcionLoteJpaRepositorio recepcionLoteRepo;

    private RegistrarRecepcionStockUseCaseImpl sut;

    @BeforeEach
    void setUp() {
        sut = new RegistrarRecepcionStockUseCaseImpl(
                ordenCompraRepo, detalleRepo, bodegaRepo, stockRepo, movimientoRepo, recepcionLoteRepo);
    }

    // ---- helpers -------------------------------------------------------

    private RegistrarRecepcionStockCommand command(int cantidad) {
        return new RegistrarRecepcionStockCommand(1, 10, 5, cantidad, "operador.test", "obs test");
    }

    private OrdenCompraJpa oc(EstadoOrdenCompra estado) {
        OrdenCompraJpa o = new OrdenCompraJpa();
        o.setIdOrdenCompra(1);
        o.setEstado(estado);
        return o;
    }

    private OrdenCompraDetalleJpa detalle(TipoItemInventario tipo,
                                          EstadoOrdenCompraDetalle estado,
                                          int solicitada,
                                          int recibida) {
        ConsumibleJpa consumible = new ConsumibleJpa();
        consumible.setIdConsumible(20);

        OrdenCompraDetalleJpa d = new OrdenCompraDetalleJpa();
        d.setTipoItem(tipo);
        d.setEstado(estado);
        d.setCantidadSolicitada(solicitada);
        d.setCantidadRecibida(recibida);
        d.setConsumible(consumible);
        return d;
    }

    private BodegaJpa bodega() {
        BodegaJpa b = new BodegaJpa();
        b.setIdBodega(5);
        return b;
    }

    // ---- happy path ----------------------------------------------------

    @Nested
    @DisplayName("ejecutar() — camino feliz")
    class HappyPath {

        @Test
        @DisplayName("registra recepcion STOCK, crea lote APLICADO, incrementa stock y movimiento")
        void registraStockExitosamente() {
            OrdenCompraJpa ocEmitida = oc(EstadoOrdenCompra.EMITIDA);
            OrdenCompraDetalleJpa det = detalle(TipoItemInventario.STOCK,
                    EstadoOrdenCompraDetalle.PENDIENTE, 5, 0);
            BodegaJpa bodega = bodega();

            RecepcionLoteJpa loteEsperado = new RecepcionLoteJpa();

            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(ocEmitida));
            when(detalleRepo.findById(10)).thenReturn(Optional.of(det));
            when(bodegaRepo.findById(5)).thenReturn(Optional.of(bodega));
            when(stockRepo.findByBodega_IdBodegaAndConsumible_IdConsumible(5, 20))
                    .thenReturn(Optional.empty());
            when(recepcionLoteRepo.save(any())).thenReturn(loteEsperado);
            when(stockRepo.save(any())).thenReturn(new StockConsumibleBodegaJpa());
            when(movimientoRepo.save(any())).thenReturn(null);
            when(detalleRepo.save(any())).thenReturn(det);
            when(detalleRepo.findByOrdenCompra_IdOrdenCompra(1)).thenReturn(List.of(det));
            when(ordenCompraRepo.save(any())).thenReturn(ocEmitida);

            RecepcionLoteJpa resultado = sut.ejecutar(command(5));

            assertThat(resultado).isSameAs(loteEsperado);
            verify(recepcionLoteRepo).save(any(RecepcionLoteJpa.class));
            verify(stockRepo).save(any(StockConsumibleBodegaJpa.class));
            verify(movimientoRepo).save(any());
            verify(detalleRepo).save(det);
            verify(ordenCompraRepo).save(ocEmitida);
            assertThat(det.getCantidadRecibida()).isEqualTo(5);
        }

        @Test
        @DisplayName("incrementa stock existente en bodega en lugar de crear uno nuevo")
        void incrementaStockExistente() {
            OrdenCompraJpa ocParcial = oc(EstadoOrdenCompra.RECEPCION_PARCIAL);
            OrdenCompraDetalleJpa det = detalle(TipoItemInventario.STOCK,
                    EstadoOrdenCompraDetalle.PARCIAL, 10, 3);
            BodegaJpa bodega = bodega();

            StockConsumibleBodegaJpa stockExistente = new StockConsumibleBodegaJpa();
            stockExistente.setCantidad(3);

            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(ocParcial));
            when(detalleRepo.findById(10)).thenReturn(Optional.of(det));
            when(bodegaRepo.findById(5)).thenReturn(Optional.of(bodega));
            when(stockRepo.findByBodega_IdBodegaAndConsumible_IdConsumible(5, 20))
                    .thenReturn(Optional.of(stockExistente));
            when(recepcionLoteRepo.save(any())).thenReturn(new RecepcionLoteJpa());
            when(stockRepo.save(any())).thenReturn(stockExistente);
            when(movimientoRepo.save(any())).thenReturn(null);
            when(detalleRepo.save(any())).thenReturn(det);
            when(detalleRepo.findByOrdenCompra_IdOrdenCompra(1)).thenReturn(List.of(det));
            when(ordenCompraRepo.save(any())).thenReturn(ocParcial);

            sut.ejecutar(command(4));

            assertThat(stockExistente.getCantidad()).isEqualTo(7);
        }
    }

    // ---- validaciones de OC --------------------------------------------

    @Nested
    @DisplayName("validaciones de OC")
    class ValidacionesOC {

        @Test
        @DisplayName("lanza RecursoNoEncontradoException si OC no existe")
        void ocNoEncontrada() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.ejecutar(command(5)))
                    .isInstanceOf(RecursoNoEncontradoException.class);

            verify(detalleRepo, never()).findById(anyInt());
        }

        @Test
        @DisplayName("lanza RecepcionNoPermitidaException si OC está en BORRADOR")
        void ocEnBorrador() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.BORRADOR)));

            assertThatThrownBy(() -> sut.ejecutar(command(5)))
                    .isInstanceOf(RecepcionNoPermitidaException.class);
        }

        @Test
        @DisplayName("lanza RecepcionNoPermitidaException si OC ya está RECIBIDA")
        void ocYaRecibida() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.RECIBIDA)));

            assertThatThrownBy(() -> sut.ejecutar(command(5)))
                    .isInstanceOf(RecepcionNoPermitidaException.class);
        }

        @Test
        @DisplayName("lanza RecepcionNoPermitidaException si OC está CANCELADA")
        void ocCancelada() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.CANCELADA)));

            assertThatThrownBy(() -> sut.ejecutar(command(5)))
                    .isInstanceOf(RecepcionNoPermitidaException.class);
        }
    }

    // ---- validaciones de detalle ---------------------------------------

    @Nested
    @DisplayName("validaciones de detalle")
    class ValidacionesDetalle {

        @Test
        @DisplayName("lanza RecursoNoEncontradoException si detalle no existe")
        void detalleNoEncontrado() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.EMITIDA)));
            when(detalleRepo.findById(10)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.ejecutar(command(5)))
                    .isInstanceOf(RecursoNoEncontradoException.class);
        }

        @Test
        @DisplayName("bloquea tipoItem ACTIVO")
        void bloqueaTipoActivo() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.EMITIDA)));
            when(detalleRepo.findById(10)).thenReturn(Optional.of(
                    detalle(TipoItemInventario.ACTIVO, EstadoOrdenCompraDetalle.PENDIENTE, 5, 0)));

            assertThatThrownBy(() -> sut.ejecutar(command(5)))
                    .isInstanceOf(RecepcionNoPermitidaException.class)
                    .hasMessageContaining("STOCK");
        }

        @Test
        @DisplayName("bloquea tipoItem CONSUMIBLE legado")
        void bloqueaTipoConsumibleLegado() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.EMITIDA)));
            when(detalleRepo.findById(10)).thenReturn(Optional.of(
                    detalle(TipoItemInventario.CONSUMIBLE, EstadoOrdenCompraDetalle.PENDIENTE, 5, 0)));

            assertThatThrownBy(() -> sut.ejecutar(command(5)))
                    .isInstanceOf(RecepcionNoPermitidaException.class)
                    .hasMessageContaining("STOCK");
        }

        @Test
        @DisplayName("bloquea detalle con estado COMPLETO")
        void bloqueaDetalleCompleto() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.RECEPCION_PARCIAL)));
            when(detalleRepo.findById(10)).thenReturn(Optional.of(
                    detalle(TipoItemInventario.STOCK, EstadoOrdenCompraDetalle.COMPLETO, 5, 5)));

            assertThatThrownBy(() -> sut.ejecutar(command(1)))
                    .isInstanceOf(RecepcionNoPermitidaException.class);
        }

        @Test
        @DisplayName("bloquea detalle con estado CANCELADO")
        void bloqueaDetalleCancelado() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.RECEPCION_PARCIAL)));
            when(detalleRepo.findById(10)).thenReturn(Optional.of(
                    detalle(TipoItemInventario.STOCK, EstadoOrdenCompraDetalle.CANCELADO, 5, 0)));

            assertThatThrownBy(() -> sut.ejecutar(command(1)))
                    .isInstanceOf(RecepcionNoPermitidaException.class);
        }
    }

    // ---- validaciones de cantidad --------------------------------------

    @Nested
    @DisplayName("validaciones de cantidad")
    class ValidacionesCantidad {

        @Test
        @DisplayName("lanza CantidadExcedidaException si cantidad supera lo pendiente")
        void cantidadExcedePendiente() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.EMITIDA)));
            when(detalleRepo.findById(10)).thenReturn(Optional.of(
                    detalle(TipoItemInventario.STOCK, EstadoOrdenCompraDetalle.PENDIENTE, 5, 0)));

            assertThatThrownBy(() -> sut.ejecutar(command(6)))
                    .isInstanceOf(CantidadExcedidaException.class);
        }

        @Test
        @DisplayName("lanza CantidadInvalidaException si cantidad es cero")
        void cantidadEsCero() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.EMITIDA)));
            when(detalleRepo.findById(10)).thenReturn(Optional.of(
                    detalle(TipoItemInventario.STOCK, EstadoOrdenCompraDetalle.PENDIENTE, 5, 0)));

            assertThatThrownBy(() -> sut.ejecutar(command(0)))
                    .isInstanceOf(CantidadInvalidaException.class);
        }

        @Test
        @DisplayName("lanza CantidadInvalidaException si cantidad es negativa")
        void cantidadEsNegativa() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.EMITIDA)));
            when(detalleRepo.findById(10)).thenReturn(Optional.of(
                    detalle(TipoItemInventario.STOCK, EstadoOrdenCompraDetalle.PENDIENTE, 5, 0)));

            assertThatThrownBy(() -> sut.ejecutar(command(-1)))
                    .isInstanceOf(CantidadInvalidaException.class);
        }
    }
}
