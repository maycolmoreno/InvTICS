package com.uisrael.gestionactivosapi.dominio.servicios.inventario;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompra;
import com.uisrael.gestionactivosapi.dominio.excepciones.EstadoInvalidoException;

class OrdenCompraStateMachineTest {

    private final OrdenCompraStateMachine stateMachine = new OrdenCompraStateMachine();

    @Test
    void permiteTransicionesValidas() {
        stateMachine.validarTransicion(EstadoOrdenCompra.BORRADOR, EstadoOrdenCompra.EMITIDA);
        stateMachine.validarTransicion(EstadoOrdenCompra.BORRADOR, EstadoOrdenCompra.CANCELADA);
        stateMachine.validarTransicion(EstadoOrdenCompra.EMITIDA, EstadoOrdenCompra.RECEPCION_PARCIAL);
        stateMachine.validarTransicion(EstadoOrdenCompra.EMITIDA, EstadoOrdenCompra.CANCELADA);
        stateMachine.validarTransicion(EstadoOrdenCompra.RECEPCION_PARCIAL, EstadoOrdenCompra.RECIBIDA);
    }

    @Test
    void bloqueaTransicionInvalida() {
        assertThatThrownBy(() -> stateMachine.validarTransicion(
                EstadoOrdenCompra.BORRADOR,
                EstadoOrdenCompra.RECIBIDA))
                .isInstanceOf(EstadoInvalidoException.class)
                .hasMessageContaining("BORRADOR -> RECIBIDA");
    }

    @Test
    void bloqueaEstadoNulo() {
        assertThatThrownBy(() -> stateMachine.validarTransicion(null, EstadoOrdenCompra.EMITIDA))
                .isInstanceOf(EstadoInvalidoException.class);
    }

    @Test
    void bloqueaDestinoNulo() {
        assertThatThrownBy(() -> stateMachine.validarTransicion(EstadoOrdenCompra.EMITIDA, null))
                .isInstanceOf(EstadoInvalidoException.class);
    }

    @Test
    void bloqueaTransicionDesdeEstadoTerminalRecibida() {
        assertThatThrownBy(() -> stateMachine.validarTransicion(
                EstadoOrdenCompra.RECIBIDA, EstadoOrdenCompra.EMITIDA))
                .isInstanceOf(EstadoInvalidoException.class);
        assertThatThrownBy(() -> stateMachine.validarTransicion(
                EstadoOrdenCompra.RECIBIDA, EstadoOrdenCompra.CANCELADA))
                .isInstanceOf(EstadoInvalidoException.class);
    }

    @Test
    void bloqueaTransicionDesdeEstadoTerminalCancelada() {
        assertThatThrownBy(() -> stateMachine.validarTransicion(
                EstadoOrdenCompra.CANCELADA, EstadoOrdenCompra.EMITIDA))
                .isInstanceOf(EstadoInvalidoException.class);
        assertThatThrownBy(() -> stateMachine.validarTransicion(
                EstadoOrdenCompra.CANCELADA, EstadoOrdenCompra.RECIBIDA))
                .isInstanceOf(EstadoInvalidoException.class);
    }
}
