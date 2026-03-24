package com.uisrael.consumogestionactivosapi.exception;

/**
 * Excepción base para servicio de equipos.
 * Todos los errores relacionados a equipos extienden esta clase.
 */
public class EquipoServiceException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private String codigo;
    
    public EquipoServiceException(String mensaje) {
        super(mensaje);
        this.codigo = "EQUIPO_ERROR";
    }
    
    public EquipoServiceException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }
    
    public EquipoServiceException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = "EQUIPO_ERROR";
    }
    
    public EquipoServiceException(String codigo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = codigo;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
