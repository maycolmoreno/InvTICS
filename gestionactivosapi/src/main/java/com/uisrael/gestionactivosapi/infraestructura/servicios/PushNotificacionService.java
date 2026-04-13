package com.uisrael.gestionactivosapi.infraestructura.servicios;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PushNotificacionService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificacionService.class);

    @Nullable
    private final FirebaseMessaging firebaseMessaging;
    private final IUsuariosJpaRepositorio usuariosRepo;

    public boolean isDisponible() {
        return firebaseMessaging != null;
    }

    /**
     * Envía una push notification al dispositivo del usuario.
     *
     * @param usuarioId ID del usuario destino
     * @param titulo    título de la notificación
     * @param cuerpo    cuerpo del mensaje
     * @param url       URL de acción (opcional, se envía como data)
     * @return true si se envió exitosamente
     */
    public boolean enviar(Integer usuarioId, String titulo, String cuerpo, String url) {
        if (firebaseMessaging == null) {
            log.debug("Push no enviado: Firebase no configurado.");
            return false;
        }

        UsuariosJpa usuario = usuariosRepo.findById(usuarioId).orElse(null);
        if (usuario == null || usuario.getFcmToken() == null || usuario.getFcmToken().isBlank()) {
            log.debug("Push no enviado: usuario {} sin token FCM.", usuarioId);
            return false;
        }

        Message.Builder builder = Message.builder()
                .setToken(usuario.getFcmToken())
                .setNotification(Notification.builder()
                        .setTitle(titulo)
                        .setBody(cuerpo)
                        .build())
                .putData("tipo", "NOTIFICACION")
                .putData("titulo", titulo)
                .putData("cuerpo", cuerpo);

        if (url != null && !url.isBlank()) {
            builder.putData("url", url);
        }

        try {
            String messageId = firebaseMessaging.send(builder.build());
            log.info("Push enviado a usuario {} (messageId={})", usuarioId, messageId);
            return true;
        } catch (FirebaseMessagingException e) {
            log.warn("Error al enviar push a usuario {}: {}", usuarioId, e.getMessage());
            if ("UNREGISTERED".equals(e.getMessagingErrorCode() != null ? e.getMessagingErrorCode().name() : "")) {
                log.info("Token FCM invalido para usuario {}. Limpiando token.", usuarioId);
                usuario.setFcmToken(null);
                usuariosRepo.save(usuario);
            }
            return false;
        }
    }

    /**
     * Registra o actualiza el token FCM de un usuario.
     */
    public void registrarToken(Integer usuarioId, String fcmToken) {
        UsuariosJpa usuario = usuariosRepo.findById(usuarioId).orElse(null);
        if (usuario == null) {
            log.warn("No se pudo registrar token FCM: usuario {} no encontrado.", usuarioId);
            return;
        }
        usuario.setFcmToken(fcmToken);
        usuariosRepo.save(usuario);
        log.info("Token FCM registrado para usuario {}", usuarioId);
    }

    /**
     * Limpia el token FCM de un usuario (al cerrar sesión).
     */
    public void limpiarToken(Integer usuarioId) {
        UsuariosJpa usuario = usuariosRepo.findById(usuarioId).orElse(null);
        if (usuario == null) return;
        usuario.setFcmToken(null);
        usuariosRepo.save(usuario);
        log.info("Token FCM limpiado para usuario {}", usuarioId);
    }
}
