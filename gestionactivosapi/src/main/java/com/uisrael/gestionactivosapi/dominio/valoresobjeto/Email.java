package com.uisrael.gestionactivosapi.dominio.valoresobjeto;

import java.util.regex.Pattern;
import com.uisrael.gestionactivosapi.dominio.excepciones.ValidacionNegocioException;

/**
 * Value object que representa un email válido.
 * Encapsula la validación de formato de correo electrónico.
 */
public class Email {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$");
    
    private final String valor;
    
    public Email(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionNegocioException("El email no puede estar vacío");
        }
        if (!EMAIL_PATTERN.matcher(valor).matches()) {
            throw new ValidacionNegocioException("Formato de email inválido: " + valor);
        }
        this.valor = valor.toLowerCase().trim();
    }
    
    public String getValor() {
        return valor;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Email email = (Email) obj;
        return valor.equals(email.valor);
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
