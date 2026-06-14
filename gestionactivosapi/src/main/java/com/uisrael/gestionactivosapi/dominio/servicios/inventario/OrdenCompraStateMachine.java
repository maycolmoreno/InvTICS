package com.uisrael.gestionactivosapi.dominio.servicios.inventario;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompra;
import com.uisrael.gestionactivosapi.dominio.excepciones.EstadoInvalidoException;

public class OrdenCompraStateMachine {

    private static final Map<EstadoOrdenCompra, Set<EstadoOrdenCompra>> TRANSICIONES = new EnumMap<>(EstadoOrdenCompra.class);

    static {
        TRANSICIONES.put(EstadoOrdenCompra.BORRADOR, EnumSet.of(
                EstadoOrdenCompra.EMITIDA,
                EstadoOrdenCompra.CANCELADA
        ));
        TRANSICIONES.put(EstadoOrdenCompra.EMITIDA, EnumSet.of(
                EstadoOrdenCompra.RECEPCION_PARCIAL,
                EstadoOrdenCompra.RECIBIDA,
                EstadoOrdenCompra.CANCELADA
        ));
        TRANSICIONES.put(EstadoOrdenCompra.RECEPCION_PARCIAL, EnumSet.of(
                EstadoOrdenCompra.RECIBIDA
        ));
    }

    public void validarTransicion(EstadoOrdenCompra actual, EstadoOrdenCompra destino) {
        if (actual == null || destino == null) {
            throw new EstadoInvalidoException("El estado actual y el estado destino son obligatorios");
        }
        if (!TRANSICIONES.getOrDefault(actual, Set.of()).contains(destino)) {
            throw new EstadoInvalidoException(
                    "Transicion de orden de compra no permitida: " + actual + " -> " + destino);
        }
    }
}
