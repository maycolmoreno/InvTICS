package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos.RegistrarRecepcionActivoCommand;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompra;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoItemInventario;
import com.uisrael.gestionactivosapi.dominio.excepciones.CantidadExcedidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecepcionNoPermitidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.BodegaJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CategoriaEquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MarcasJpa;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistrarRecepcionActivoUseCaseImpl")
class RegistrarRecepcionActivoUseCaseImplTest {

    @Mock private IOrdenCompraJpaRepositorio ordenCompraRepo;
    @Mock private IOrdenCompraDetalleJpaRepositorio detalleRepo;
    @Mock private IBodegaJpaRepositorio bodegaRepo;
    @Mock private IMarcasJpaRepositorio marcaRepo;
    @Mock private ICategoriaEquiposJpaRepositorio categoriaRepo;
    @Mock private IEquiposJpaRepositorio equiposRepo;
    @Mock private IMovimientoInventarioJpaRepositorio movimientoRepo;
    @Mock private IRecepcionLoteJpaRepositorio recepcionLoteRepo;

    private RegistrarRecepcionActivoUseCaseImpl sut;

    @BeforeEach
    void setUp() {
        sut = new RegistrarRecepcionActivoUseCaseImpl(
                ordenCompraRepo, detalleRepo, bodegaRepo, marcaRepo, categoriaRepo,
                equiposRepo, movimientoRepo, recepcionLoteRepo);
    }

    // ---- helpers --------------------------------------------------------

    private RegistrarRecepcionActivoCommand command() {
        return new RegistrarRecepcionActivoCommand(
                1, 10, 5, 3, 2, "ThinkPad X1", "SN-ABC-001", "BUENO",
                "operador.test", "obs", null, null, null, null, null, null, null, null, null);
    }

    private OrdenCompraJpa oc(EstadoOrdenCompra estado) {
        OrdenCompraJpa o = new OrdenCompraJpa();
        o.setIdOrdenCompra(1);
        o.setEstado(estado);
        return o;
    }

    private OrdenCompraDetalleJpa detalle(TipoItemInventario tipo,
                                          EstadoOrdenCompraDetalle estado,
                                          int solicitada, int recibida) {
        OrdenCompraDetalleJpa d = new OrdenCompraDetalleJpa();
        d.setTipoItem(tipo);
        d.setEstado(estado);
        d.setCantidadSolicitada(solicitada);
        d.setCantidadRecibida(recibida);
        return d;
    }

    private void stubHappyPath(OrdenCompraJpa oc, OrdenCompraDetalleJpa det) {
        BodegaJpa bodega = new BodegaJpa();
        bodega.setIdBodega(5);
        MarcasJpa marca = new MarcasJpa();
        CategoriaEquiposJpa cat = new CategoriaEquiposJpa();
        RecepcionLoteJpa lote = new RecepcionLoteJpa();
        EquiposJpa equipo = new EquiposJpa();

        when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc));
        when(detalleRepo.findById(10)).thenReturn(Optional.of(det));
        when(equiposRepo.existsBySerialIgnoreCase("SN-ABC-001")).thenReturn(false);
        when(bodegaRepo.findById(5)).thenReturn(Optional.of(bodega));
        when(marcaRepo.findById(2)).thenReturn(Optional.of(marca));
        when(categoriaRepo.findById(3)).thenReturn(Optional.of(cat));
        when(recepcionLoteRepo.save(any())).thenReturn(lote);
        when(equiposRepo.save(any())).thenReturn(equipo);
        when(movimientoRepo.save(any())).thenReturn(null);
        when(detalleRepo.save(any())).thenReturn(det);
        when(detalleRepo.findByOrdenCompra_IdOrdenCompra(1)).thenReturn(List.of(det));
        when(ordenCompraRepo.save(any())).thenReturn(oc);
    }

    // ---- happy path -----------------------------------------------------

    @Nested
    @DisplayName("ejecutar() — camino feliz")
    class HappyPath {

        @Test
        @DisplayName("recibe activo, crea equipo en EN_BODEGA, lote APLICADO y movimiento INGRESO_ACTIVO")
        void recibeActivoExitosamente() {
            OrdenCompraJpa ocEmitida = oc(EstadoOrdenCompra.EMITIDA);
            OrdenCompraDetalleJpa det = detalle(TipoItemInventario.ACTIVO,
                    EstadoOrdenCompraDetalle.PENDIENTE, 3, 0);
            stubHappyPath(ocEmitida, det);

            RecepcionLoteJpa resultado = sut.ejecutar(command());

            assertThat(resultado).isNotNull();
            verify(recepcionLoteRepo).save(any(RecepcionLoteJpa.class));
            verify(equiposRepo).save(any(EquiposJpa.class));
            verify(movimientoRepo).save(any());
            verify(detalleRepo).save(det);
            verify(ordenCompraRepo).save(ocEmitida);
            assertThat(det.getCantidadRecibida()).isEqualTo(1);
        }

        @Test
        @DisplayName("incrementa cantidadRecibida acumulada correctamente desde recepcion parcial")
        void acumulaCantidadRecibida() {
            OrdenCompraJpa ocParcial = oc(EstadoOrdenCompra.RECEPCION_PARCIAL);
            OrdenCompraDetalleJpa det = detalle(TipoItemInventario.ACTIVO,
                    EstadoOrdenCompraDetalle.PARCIAL, 3, 1);
            stubHappyPath(ocParcial, det);

            sut.ejecutar(command());

            assertThat(det.getCantidadRecibida()).isEqualTo(2);
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

            assertThatThrownBy(() -> sut.ejecutar(command()))
                    .isInstanceOf(RecursoNoEncontradoException.class);

            verify(detalleRepo, never()).findById(anyInt());
        }

        @Test
        @DisplayName("lanza RecepcionNoPermitidaException si OC está en BORRADOR")
        void ocEnBorrador() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.BORRADOR)));

            assertThatThrownBy(() -> sut.ejecutar(command()))
                    .isInstanceOf(RecepcionNoPermitidaException.class);
        }

        @Test
        @DisplayName("lanza RecepcionNoPermitidaException si OC está CANCELADA")
        void ocCancelada() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.CANCELADA)));

            assertThatThrownBy(() -> sut.ejecutar(command()))
                    .isInstanceOf(RecepcionNoPermitidaException.class);
        }

        @Test
        @DisplayName("lanza RecepcionNoPermitidaException si OC ya está RECIBIDA")
        void ocYaRecibida() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.RECIBIDA)));

            assertThatThrownBy(() -> sut.ejecutar(command()))
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

            assertThatThrownBy(() -> sut.ejecutar(command()))
                    .isInstanceOf(RecursoNoEncontradoException.class);
        }

        @Test
        @DisplayName("bloquea tipoItem STOCK")
        void bloqueaTipoStock() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.EMITIDA)));
            when(detalleRepo.findById(10)).thenReturn(Optional.of(
                    detalle(TipoItemInventario.STOCK, EstadoOrdenCompraDetalle.PENDIENTE, 3, 0)));

            assertThatThrownBy(() -> sut.ejecutar(command()))
                    .isInstanceOf(RecepcionNoPermitidaException.class)
                    .hasMessageContaining("ACTIVO");
        }

        @Test
        @DisplayName("bloquea tipoItem CONSUMIBLE legado")
        void bloqueaTipoConsumible() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.EMITIDA)));
            when(detalleRepo.findById(10)).thenReturn(Optional.of(
                    detalle(TipoItemInventario.CONSUMIBLE, EstadoOrdenCompraDetalle.PENDIENTE, 3, 0)));

            assertThatThrownBy(() -> sut.ejecutar(command()))
                    .isInstanceOf(RecepcionNoPermitidaException.class)
                    .hasMessageContaining("ACTIVO");
        }

        @Test
        @DisplayName("bloquea detalle COMPLETO")
        void bloqueaDetalleCompleto() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.RECEPCION_PARCIAL)));
            when(detalleRepo.findById(10)).thenReturn(Optional.of(
                    detalle(TipoItemInventario.ACTIVO, EstadoOrdenCompraDetalle.COMPLETO, 3, 3)));

            assertThatThrownBy(() -> sut.ejecutar(command()))
                    .isInstanceOf(RecepcionNoPermitidaException.class);
        }

        @Test
        @DisplayName("lanza CantidadExcedidaException si ya se recibieron todos los activos")
        void cantidadExcedida() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.RECEPCION_PARCIAL)));
            // solicitada=1, recibida=1 → pendiente=0, intentar recibir 1 → excede
            when(detalleRepo.findById(10)).thenReturn(Optional.of(
                    detalle(TipoItemInventario.ACTIVO, EstadoOrdenCompraDetalle.PARCIAL, 1, 1)));

            assertThatThrownBy(() -> sut.ejecutar(command()))
                    .isInstanceOf(CantidadExcedidaException.class);
        }
    }

    // ---- validación de serial ------------------------------------------

    @Nested
    @DisplayName("validaciones de serial")
    class ValidacionesSerial {

        @Test
        @DisplayName("lanza RecepcionNoPermitidaException si el serial ya existe")
        void serialDuplicado() {
            when(ordenCompraRepo.findById(1)).thenReturn(Optional.of(oc(EstadoOrdenCompra.EMITIDA)));
            when(detalleRepo.findById(10)).thenReturn(Optional.of(
                    detalle(TipoItemInventario.ACTIVO, EstadoOrdenCompraDetalle.PENDIENTE, 3, 0)));
            when(equiposRepo.existsBySerialIgnoreCase("SN-ABC-001")).thenReturn(true);

            assertThatThrownBy(() -> sut.ejecutar(command()))
                    .isInstanceOf(RecepcionNoPermitidaException.class)
                    .hasMessageContaining("SN-ABC-001");

            verify(bodegaRepo, never()).findById(anyInt());
            verify(recepcionLoteRepo, never()).save(any());
        }

        @Test
        @DisplayName("acepta serial nuevo sin conflicto")
        void serialNuevoAceptado() {
            OrdenCompraJpa ocEmitida = oc(EstadoOrdenCompra.EMITIDA);
            OrdenCompraDetalleJpa det = detalle(TipoItemInventario.ACTIVO,
                    EstadoOrdenCompraDetalle.PENDIENTE, 3, 0);
            stubHappyPath(ocEmitida, det);

            // No lanza excepción
            assertThat(sut.ejecutar(command())).isNotNull();
            verify(equiposRepo).existsBySerialIgnoreCase(anyString());
        }
    }
}
