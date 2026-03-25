package com.uisrael.gestionactivosapi.dominio.valoresobjeto;

import com.uisrael.gestionactivosapi.dominio.excepciones.ValidacionNegocioException;

/**
 * Value object que representa un nombre con validaciones básicas.
 * Garantiza que no sea nulo ni esté vacío.
 */
public class Nombre {
    
    private final String valor;
    
    public Nombre(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionNegocioException("El nombre no puede estar vacío");
        }
        if (valor.length() > 255) {
            throw new ValidacionNegocioException("El nombre no puede exceder 255 caracteres");
        }
        this.valor = valor.trim();
    }
    
    public String getValor() {
        return valor;
    }
    
    public boolean estaVacio() {
        return valor.isEmpty();
    }
    
    public int longitud() {
        return valor.length();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Nombre nombre = (Nombre) obj;
        return valor.equals(nombre.valor);
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
