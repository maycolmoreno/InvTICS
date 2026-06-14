package com.uisrael.gestionactivosapi.dominio.servicios.inventario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.dominio.excepciones.CantidadExcedidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.CantidadInvalidaException;

class OrdenCompraDetalleStateMachineTest {

    private final OrdenCompraDetalleStateMachine stateMachine = new OrdenCompraDetalleStateMachine();

    @Test
    void calculaPendienteCuandoNoHayRecepcion() {
        assertThat(stateMachine.calcularEstado(5, 0, EstadoOrdenCompraDetalle.PENDIENTE))
                .isEqualTo(EstadoOrdenCompraDetalle.PENDIENTE);
    }

    @Test
    void calculaParcialCuandoRecibidoEsMenorASolicitado() {
        assertThat(stateMachine.calcularEstado(5, 2, EstadoOrdenCompraDetalle.PENDIENTE))
                .isEqualTo(EstadoOrdenCompraDetalle.PARCIAL);
    }

    @Test
    void calculaCompletoCuandoRecibidoEsIgualASolicitado() {
        assertThat(stateMachine.calcularEstado(5, 5, EstadoOrdenCompraDetalle.PARCIAL))
                .isEqualTo(EstadoOrdenCompraDetalle.COMPLETO);
    }

    @Test
    void noCambiaCanceladoAutomaticamente() {
        assertThat(stateMachine.calcularEstado(5, 5, EstadoOrdenCompraDetalle.CANCELADO))
                .isEqualTo(EstadoOrdenCompraDetalle.CANCELADO);
    }

    @Test
    void rechazaCantidadRecibidaMayorASolicitada() {
        assertThatThrownBy(() -> stateMachine.calcularEstado(5, 6, EstadoOrdenCompraDetalle.PARCIAL))
                .isInstanceOf(CantidadExcedidaException.class);
    }

    @Test
    void rechazaCantidadSolicitadaInvalida() {
        assertThatThrownBy(() -> stateMachine.calcularEstado(0, 0, EstadoOrdenCompraDetalle.PENDIENTE))
                .isInstanceOf(CantidadInvalidaException.class);
    }
}
