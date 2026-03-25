package com.uisrael.gestionactivosapi.dominio.puertos.servicios;

/**
 * Puerto para el envío de correos electrónicos.
 * Define el contrato para operaciones de envío de email sin dependencias concretas.
 */
public interface EnviadorCorreoPuerto {
    
    /**
     * Envía un correo simple a un destinatario.
     * 
     * @param destinatario correo electrónico del destinatario
     * @param asunto asunto del correo
     * @param contenido contenido HTML del correo
     * @return true si se envió exitosamente
     */
    boolean enviarCorreo(String destinatario, String asunto, String contenido);
    
    /**
     * Envía un correo a múltiples destinatarios.
     * 
     * @param destinatarios array de correos
     * @param asunto asunto del correo
     * @param contenido contenido HTML
     * @return true si se envió exitosamente
     */
    boolean enviarCorreoMultiple(String[] destinatarios, String asunto, String contenido);
    
    /**
     * Envía un correo con archivo adjunto.
     * 
     * @param destinatario correo del destinatario
     * @param asunto asunto del correo
     * @param contenido contenido HTML
     * @param contenidoAdjunto byte[] del archivo
     * @param nombreArchivo nombre del archivo adjunto
     * @return true si se envió exitosamente
     */
    boolean enviarCorreoConAdjunto(String destinatario, String asunto, String contenido,
            byte[] contenidoAdjunto, String nombreArchivo);
    
    /**
     * Verifica que el servicio de correo esté disponible.
     * 
     * @return true si está disponible
     */
    boolean isDisponible();
}
