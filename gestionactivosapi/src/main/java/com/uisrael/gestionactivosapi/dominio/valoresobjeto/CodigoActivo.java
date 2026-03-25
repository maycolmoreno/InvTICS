package com.uisrael.gestionactivosapi.dominio.valoresobjeto;

import com.uisrael.gestionactivosapi.dominio.excepciones.ValidacionNegocioException;

/**
 * Value object que representa un código SAP de equipo.
 */
public class CodigoActivo {
    
    private final String valor;
    
    public CodigoActivo(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionNegocioException("El código de Activo no puede estar vacío");
        }
        if (!valor.matches("^[A-Z0-9-]{1,50}$")) {
            throw new ValidacionNegocioException("Formato de código activo inválido. Debe contener solo mayúsculas, números y guiones");
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
        CodigoActivo codigoActivo = (CodigoActivo) obj;
        return valor.equals(codigoActivo.valor);
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
