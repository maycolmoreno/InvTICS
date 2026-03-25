package com.uisrael.gestionactivosapi.dominio.valoresobjeto;

import com.uisrael.gestionactivosapi.dominio.excepciones.ValidacionNegocioException;

/**
 * Value object que representa una cédula o identificación de custodio.
 */
public class CedulaCustodio {
    
    private final String valor;
    
    public CedulaCustodio(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionNegocioException("La cédula no puede estar vacía");
        }
        if (!valor.matches("^[0-9]{6,10}$")) {
            throw new ValidacionNegocioException("Formato de cédula inválido. Debe contener 6-10 dígitos");
        }
        this.valor = valor.trim();
    }
    
    public String getValor() {
        return valor;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CedulaCustodio cedula = (CedulaCustodio) obj;
        return valor.equals(cedula.valor);
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
