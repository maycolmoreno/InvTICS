package com.uisrael.gestionactivosapi.infraestructura.adaptadores.notificacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uisrael.gestionactivosapi.dominio.entidades.Notificacion;
import com.uisrael.gestionactivosapi.dominio.puertos.servicios.EnviadorCorreoPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.servicios.ServicioNotificacionPuerto;
import com.uisrael.gestionactivosapi.infraestructura.servicios.PushNotificacionService;

public class ServicioNotificacionAdaptador implements ServicioNotificacionPuerto {

	private static final Logger log = LoggerFactory.getLogger(ServicioNotificacionAdaptador.class);

	private final EnviadorCorreoPuerto enviadorCorreo;
	private final PushNotificacionService pushNotificacionService;

	public ServicioNotificacionAdaptador(EnviadorCorreoPuerto enviadorCorreo,
			PushNotificacionService pushNotificacionService) {
		this.enviadorCorreo = enviadorCorreo;
		this.pushNotificacionService = pushNotificacionService;
	}

	@Override
	public boolean enviar(Notificacion notificacion) {
		if (notificacion == null || notificacion.getUsuarioId() == null) {
			return false;
		}
		String canal = notificacion.getCanal();
		if ("EMAIL".equalsIgnoreCase(canal)) {
			log.info("Notificacion por correo para usuario {}", notificacion.getUsuarioId());
		} else if ("PUSH".equalsIgnoreCase(canal)) {
			return pushNotificacionService.enviar(
					notificacion.getUsuarioId(),
					notificacion.getAsunto(),
					notificacion.getCuerpo(),
					null);
		}
		return true;
	}

	@Override
	public boolean enviarPorCorreo(String correoDestinatario, String asunto, String cuerpo) {
		return enviadorCorreo.enviarCorreo(correoDestinatario, asunto, cuerpo);
	}

	@Override
	public boolean enviarPorSms(String telefonoDestinatario, String mensaje) {
		log.warn("Envio por SMS no implementado. Destinatario: {}", telefonoDestinatario);
		return false;
	}

	@Override
	public boolean enviarPorPush(Integer usuarioId, String titulo, String cuerpo) {
		return pushNotificacionService.enviar(usuarioId, titulo, cuerpo, null);
	}

	@Override
	public void marcarComoEnviada(Integer notificacionId) {
		log.info("Notificacion {} marcada como enviada (no-op)", notificacionId);
	}
}
