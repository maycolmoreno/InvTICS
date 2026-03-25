package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado.
 */
public class RecursoNoEncontradoException extends ExcepcionDominio {
    
    private final String tipoEntidad;
    private final Object identificador;
    
    public RecursoNoEncontradoException(String tipoEntidad, Object identificador) {
        super(String.format("%s con identificador %s no encontrado", tipoEntidad, identificador));
        this.tipoEntidad = tipoEntidad;
        this.identificador = identificador;
    }
    
    public String getTipoEntidad() {
        return tipoEntidad;
    }
    
    public Object getIdentificador() {
        return identificador;
    }
}
