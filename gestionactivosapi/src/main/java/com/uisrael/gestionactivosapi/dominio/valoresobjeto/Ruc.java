package com.uisrael.gestionactivosapi.dominio.valoresobjeto;

import com.uisrael.gestionactivosapi.dominio.excepciones.ValidacionNegocioException;

/**
 * Value Object inmutable que representa un RUC ecuatoriano.
 * Valida que tenga exactamente 13 dígitos numéricos.
 */
public record Ruc(String valor) {

    public Ruc {
        if (valor == null || valor.isBlank()) {
            throw new ValidacionNegocioException("El RUC no puede estar vacío");
        }
        valor = valor.trim();
        if (!valor.matches("\\d{13}")) {
            throw new ValidacionNegocioException(
                "El RUC debe tener exactamente 13 dígitos numéricos. Valor recibido: " + valor);
        }
    }

    @Override
    public String toString() {
        return valor;
    }
}
