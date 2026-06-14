package com.uisrael.gestionactivosapi.dominio.servicios.inventario;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompra;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.dominio.excepciones.CantidadExcedidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.CantidadInvalidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecepcionNoPermitidaException;

class RecepcionGuardsTest {

    private final RecepcionGuards guards = new RecepcionGuards();

    @Test
    void permiteOrdenEmitidaOParcial() {
        guards.validarOrdenRecibible(EstadoOrdenCompra.EMITIDA);
        guards.validarOrdenRecibible(EstadoOrdenCompra.RECEPCION_PARCIAL);
    }

    @Test
    void bloqueaOrdenNoRecibible() {
        assertThatThrownBy(() -> guards.validarOrdenRecibible(EstadoOrdenCompra.BORRADOR))
                .isInstanceOf(RecepcionNoPermitidaException.class);
        assertThatThrownBy(() -> guards.validarOrdenRecibible(EstadoOrdenCompra.CANCELADA))
                .isInstanceOf(RecepcionNoPermitidaException.class);
        assertThatThrownBy(() -> guards.validarOrdenRecibible(EstadoOrdenCompra.RECIBIDA))
                .isInstanceOf(RecepcionNoPermitidaException.class);
    }

    @Test
    void permiteDetallePendienteOParcial() {
        guards.validarDetalleRecibible(EstadoOrdenCompraDetalle.PENDIENTE);
        guards.validarDetalleRecibible(EstadoOrdenCompraDetalle.PARCIAL);
    }

    @Test
    void bloqueaDetalleNoRecibible() {
        assertThatThrownBy(() -> guards.validarDetalleRecibible(EstadoOrdenCompraDetalle.COMPLETO))
                .isInstanceOf(RecepcionNoPermitidaException.class);
        assertThatThrownBy(() -> guards.validarDetalleRecibible(EstadoOrdenCompraDetalle.CANCELADO))
                .isInstanceOf(RecepcionNoPermitidaException.class);
    }

    @Test
    void validaCantidadContraPendiente() {
        guards.validarCantidad(2, 5, 3);
    }

    @Test
    void bloqueaCantidadCeroONegativa() {
        assertThatThrownBy(() -> guards.validarCantidad(0, 5, 0))
                .isInstanceOf(CantidadInvalidaException.class);
        assertThatThrownBy(() -> guards.validarCantidad(-1, 5, 0))
                .isInstanceOf(CantidadInvalidaException.class);
    }

    @Test
    void bloqueaCantidadMayorAPendiente() {
        assertThatThrownBy(() -> guards.validarCantidad(3, 5, 3))
                .isInstanceOf(CantidadExcedidaException.class);
    }
}
