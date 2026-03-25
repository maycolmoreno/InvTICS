package com.uisrael.gestionactivosapi.dominio.puertos.servicios;

import com.uisrael.gestionactivosapi.dominio.entidades.Notificacion;

/**
 * Puerto para el envío de notificaciones a través de múltiples canales.
 * Define el contrato para operaciones de envío de notificaciones sin dependencias concretas.
 */
public interface ServicioNotificacionPuerto {
    
    /**
     * Envía una notificación al usuariousando el canal preferido.
     * 
     * @param notificacion la notificación a enviar
     * @return true si se envió exitosamente
     */
    boolean enviar(Notificacion notificacion);
    
    /**
     * Envía una notificación por correo electrónico.
     * 
     * @param correoDestinatario correo del destinatario
     * @param asunto asunto de la notificación
     * @param cuerpo cuerpo del mensaje
     * @return true si se envió exitosamente
     */
    boolean enviarPorCorreo(String correoDestinatario, String asunto, String cuerpo);
    
    /**
     * Envía una notificación por SMS.
     * 
     * @param telefonoDestinatario número de teléfono
     * @param mensaje cuerpo del SMS (máx 160 caracteres)
     * @return true si se envió exitosamente
     */
    boolean enviarPorSms(String telefonoDestinatario, String mensaje);
    
    /**
     * Envía una notificación por push a una aplicación móvil.
     * 
     * @param usuarioId ID del usuario
     * @param titulo título de la notificación
     * @param cuerpo cuerpo del mensaje
     * @return true si se envió exitosamente
     */
    boolean enviarPorPush(Integer usuarioId, String titulo, String cuerpo);
    
    /**
     * Marca una notificación como enviada.
     * 
     * @param notificacionId ID de la notificación
     */
    void marcarComoEnviada(Integer notificacionId);
}
