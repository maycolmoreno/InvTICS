package com.uisrael.gestionactivosapi.infraestructura.adaptadores.correo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.uisrael.gestionactivosapi.dominio.puertos.servicios.EnviadorCorreoPuerto;

import jakarta.mail.internet.MimeMessage;

public class EnviadorCorreoAdaptador implements EnviadorCorreoPuerto {

	private static final Logger log = LoggerFactory.getLogger(EnviadorCorreoAdaptador.class);

	private final JavaMailSender mailSender;
	private final String remitente;
	private final String nombreRemitente;

	public EnviadorCorreoAdaptador(JavaMailSender mailSender, String remitente, String nombreRemitente) {
		this.mailSender = mailSender;
		this.remitente = remitente;
		this.nombreRemitente = nombreRemitente;
	}

	@Override
	public boolean enviarCorreo(String destinatario, String asunto, String contenido) {
		if (destinatario == null || destinatario.isBlank()) {
			log.warn("No se envio correo: destinatario vacio");
			return false;
		}
		try {
			MimeMessage mensaje = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
			helper.setFrom(remitente, nombreRemitente);
			helper.setTo(destinatario);
			helper.setSubject(asunto);
			helper.setText(contenido, true);
			mailSender.send(mensaje);
			log.info("Correo enviado a {}", destinatario);
			return true;
		} catch (Exception e) {
			log.error("Error al enviar correo a {}: {}", destinatario, e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean enviarCorreoMultiple(String[] destinatarios, String asunto, String contenido) {
		if (destinatarios == null || destinatarios.length == 0) {
			return false;
		}
		try {
			MimeMessage mensaje = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
			helper.setFrom(remitente, nombreRemitente);
			helper.setTo(destinatarios);
			helper.setSubject(asunto);
			helper.setText(contenido, true);
			mailSender.send(mensaje);
			log.info("Correo multiple enviado a {} destinatarios", destinatarios.length);
			return true;
		} catch (Exception e) {
			log.error("Error al enviar correo multiple: {}", e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean enviarCorreoConAdjunto(String destinatario, String asunto, String contenido,
			byte[] contenidoAdjunto, String nombreArchivo) {
		if (destinatario == null || destinatario.isBlank()) {
			return false;
		}
		try {
			MimeMessage mensaje = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
			helper.setFrom(remitente, nombreRemitente);
			helper.setTo(destinatario);
			helper.setSubject(asunto);
			helper.setText(contenido, true);
			helper.addAttachment(nombreArchivo, new ByteArrayResource(contenidoAdjunto));
			mailSender.send(mensaje);
			log.info("Correo con adjunto enviado a {}", destinatario);
			return true;
		} catch (Exception e) {
			log.error("Error al enviar correo con adjunto a {}: {}", destinatario, e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean isDisponible() {
		return remitente != null && !remitente.isBlank();
	}
}
