package com.uisrael.gestionactivosapi.dominio.servicios.inventario;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompra;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraDetalleJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraJpa;

class RecalcularEstadosServiceTest {

    private final RecalcularEstadosService service = new RecalcularEstadosService();

    @Test
    void recalculaDetalle() {
        OrdenCompraDetalleJpa detalle = detalle(5, 2, EstadoOrdenCompraDetalle.PENDIENTE);

        EstadoOrdenCompraDetalle estado = service.recalcularDetalle(detalle);

        assertThat(estado).isEqualTo(EstadoOrdenCompraDetalle.PARCIAL);
        assertThat(detalle.getEstado()).isEqualTo(EstadoOrdenCompraDetalle.PARCIAL);
    }

    @Test
    void ordenPasaARecibidaSiTodosLosDetallesEstanCerrados() {
        OrdenCompraJpa orden = orden(EstadoOrdenCompra.RECEPCION_PARCIAL);

        EstadoOrdenCompra estado = service.recalcularOrden(orden, List.of(
                detalle(5, 5, EstadoOrdenCompraDetalle.PARCIAL),
                detalle(3, 0, EstadoOrdenCompraDetalle.CANCELADO)
        ));

        assertThat(estado).isEqualTo(EstadoOrdenCompra.RECIBIDA);
        assertThat(orden.getEstado()).isEqualTo(EstadoOrdenCompra.RECIBIDA);
    }

    @Test
    void ordenPasaARecepcionParcialSiAlgunDetalleEsParcial() {
        OrdenCompraJpa orden = orden(EstadoOrdenCompra.EMITIDA);

        EstadoOrdenCompra estado = service.recalcularOrden(orden, List.of(
                detalle(5, 2, EstadoOrdenCompraDetalle.PENDIENTE),
                detalle(3, 0, EstadoOrdenCompraDetalle.PENDIENTE)
        ));

        assertThat(estado).isEqualTo(EstadoOrdenCompra.RECEPCION_PARCIAL);
        assertThat(orden.getEstado()).isEqualTo(EstadoOrdenCompra.RECEPCION_PARCIAL);
    }

    @Test
    void ordenEmitidaSinRecepcionSeMantieneEmitida() {
        OrdenCompraJpa orden = orden(EstadoOrdenCompra.EMITIDA);

        EstadoOrdenCompra estado = service.recalcularOrden(orden, List.of(
                detalle(5, 0, EstadoOrdenCompraDetalle.PENDIENTE),
                detalle(3, 0, EstadoOrdenCompraDetalle.PENDIENTE)
        ));

        assertThat(estado).isEqualTo(EstadoOrdenCompra.EMITIDA);
        assertThat(orden.getEstado()).isEqualTo(EstadoOrdenCompra.EMITIDA);
    }

    @Test
    void recalcularOrdenNoTocaOrdenRecibida() {
        OrdenCompraJpa orden = orden(EstadoOrdenCompra.RECIBIDA);

        EstadoOrdenCompra estado = service.recalcularOrden(orden, List.of(
                detalle(5, 5, EstadoOrdenCompraDetalle.COMPLETO)
        ));

        assertThat(estado).isEqualTo(EstadoOrdenCompra.RECIBIDA);
        assertThat(orden.getEstado()).isEqualTo(EstadoOrdenCompra.RECIBIDA);
    }

    @Test
    void recalcularOrdenConListaVaciaRetornaEstadoActual() {
        OrdenCompraJpa orden = orden(EstadoOrdenCompra.EMITIDA);

        EstadoOrdenCompra estado = service.recalcularOrden(orden, List.of());

        assertThat(estado).isEqualTo(EstadoOrdenCompra.EMITIDA);
        assertThat(orden.getEstado()).isEqualTo(EstadoOrdenCompra.EMITIDA);
    }

    @Test
    void noTocaOrdenBorradorOCancelada() {
        OrdenCompraJpa borrador = orden(EstadoOrdenCompra.BORRADOR);
        OrdenCompraJpa cancelada = orden(EstadoOrdenCompra.CANCELADA);

        assertThat(service.recalcularOrden(borrador, List.of(detalle(1, 1, EstadoOrdenCompraDetalle.PENDIENTE))))
                .isEqualTo(EstadoOrdenCompra.BORRADOR);
        assertThat(service.recalcularOrden(cancelada, List.of(detalle(1, 1, EstadoOrdenCompraDetalle.PENDIENTE))))
                .isEqualTo(EstadoOrdenCompra.CANCELADA);
    }

    private OrdenCompraJpa orden(EstadoOrdenCompra estado) {
        OrdenCompraJpa orden = new OrdenCompraJpa();
        orden.setEstado(estado);
        return orden;
    }

    private OrdenCompraDetalleJpa detalle(
            Integer solicitada,
            Integer recibida,
            EstadoOrdenCompraDetalle estado) {
        OrdenCompraDetalleJpa detalle = new OrdenCompraDetalleJpa();
        detalle.setCantidadSolicitada(solicitada);
        detalle.setCantidadRecibida(recibida);
        detalle.setEstado(estado);
        return detalle;
    }
}
