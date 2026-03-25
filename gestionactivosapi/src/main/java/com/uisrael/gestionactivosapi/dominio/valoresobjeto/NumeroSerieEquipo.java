package com.uisrael.gestionactivosapi.dominio.valoresobjeto;

import com.uisrael.gestionactivosapi.dominio.excepciones.ValidacionNegocioException;

/**
 * Value object que representa el número de serie de un equipo.
 */
public class NumeroSerieEquipo {
    
    private final String valor;
    
    public NumeroSerieEquipo(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionNegocioException("El número de serie no puede estar vacío");
        }
        if (valor.length() > 100) {
            throw new ValidacionNegocioException("El número de serie no puede exceder 100 caracteres");
        }
        this.valor = valor.toUpperCase().trim();
    }
    
    public String getValor() {
        return valor;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NumeroSerieEquipo numeroSerie = (NumeroSerieEquipo) obj;
        return valor.equals(numeroSerie.valor);
    }
    
    @Override
    public int hashCode() {
        return valor.hashCode();
    }
    
    @Override
    public String toString() {
        return valor;
    }
}
