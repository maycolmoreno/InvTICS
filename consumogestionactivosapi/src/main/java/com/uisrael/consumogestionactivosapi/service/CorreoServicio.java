package com.uisrael.consumogestionactivosapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.mail.internet.MimeMessage;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CorreoServicio {

	private static final Logger log = LoggerFactory.getLogger(CorreoServicio.class);

	private final JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String remitente;

	@Value("${app.mail.from-name:CRESIO - Gestion de Activos}")
	private String nombreRemitente;

	public CorreoServicio(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * Envía el acta como adjunto PDF al custodio.
	 *
	 * @param destinatario correo del custodio que recibe los activos
	 * @param nombreCustodio nombre del custodio
	 * @param numActa número del acta
	 * @param pdfBytes contenido del PDF generado
	 * @param tipoMovimiento tipo de movimiento (ACTA_INICIAL, ASIGNACION, TRASLADO, BAJA)
	 */
	public void enviarActaAsignacion(String destinatario, String nombreCustodio,
			String numActa, byte[] pdfBytes, String tipoMovimiento) {

		if (destinatario == null || destinatario.isBlank()) {
			log.warn("No se envió correo: el custodio '{}' no tiene correo registrado", nombreCustodio);
			return;
		}

		if (tipoMovimiento == null || tipoMovimiento.isBlank()) tipoMovimiento = "ASIGNACION";

		String etiqueta = switch (tipoMovimiento) {
			case "ACTA_INICIAL" -> "Acta Inicial";
			case "TRASLADO"     -> "Traslado";
			case "BAJA"         -> "Baja";
			default             -> "Acta de Asignacion";
		};

		String nombreArchivo = switch (tipoMovimiento) {
			case "ACTA_INICIAL" -> "Acta_Inicial_" + numActa + ".pdf";
			case "TRASLADO"     -> "Acta_Traslado_" + numActa + ".pdf";
			case "BAJA"         -> "Acta_Baja_" + numActa + ".pdf";
			default             -> "Acta_Asignacion_" + numActa + ".pdf";
		};

		try {
			MimeMessage mensaje = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

			helper.setFrom(remitente, nombreRemitente);
			helper.setTo(destinatario);
			helper.setSubject(etiqueta + " de Activos No. " + numActa);

			String cuerpo = """
					<div style="font-family: Arial, sans-serif; color: #333;">
					  <h2 style="color: #6a1b9a;">CRESIO - Gesti&oacute;n de Activos</h2>
					  <p>Estimado/a <strong>%s</strong>,</p>
					  <p>Se adjunta el <strong>%s N.&ordm; %s</strong> correspondiente
					     a los activos fijos bajo su responsabilidad.</p>
					  <p>Por favor revise el documento adjunto y conserve una copia para sus registros.</p>
					  <br/>
					  <p style="font-size: 12px; color: #888;">
					    Este es un mensaje autom&aacute;tico del sistema de Gesti&oacute;n de Activos CRESIO.
					    No responda a este correo.
					  </p>
					</div>
					""".formatted(nombreCustodio, etiqueta, numActa);

			helper.setText(cuerpo, true);

			helper.addAttachment(nombreArchivo, new ByteArrayResource(pdfBytes),
					"application/pdf");

			mailSender.send(mensaje);
			log.info("Correo con {} enviado a {}", etiqueta, destinatario);

		} catch (Exception e) {
			log.error("Error al enviar correo a {}: {}", destinatario, e.getMessage(), e);
		}
	}

	/**
	 * Envia el acta como adjunto PDF al cliente e incluye el detalle del mantenimiento en el cuerpo.
	 */
	public void enviarActaAsignacionConMantenimiento(String destinatario, String nombreCliente,
			String numActa, byte[] pdfBytes, String tipoMovimiento,
			LocalDate fechaMantenimiento, String tipoMantenimiento, String detalleMantenimiento,
			List<MultipartFile> imagenes) {

		if (destinatario == null || destinatario.isBlank()) {
			log.warn("No se envio correo: destinatario vacio");
			return;
		}

		if (tipoMovimiento == null || tipoMovimiento.isBlank()) tipoMovimiento = "ASIGNACION";

		String etiqueta = switch (tipoMovimiento) {
			case "ACTA_INICIAL" -> "Acta Inicial";
			case "TRASLADO"     -> "Traslado";
			case "BAJA"         -> "Baja";
			default             -> "Acta de Asignacion";
		};

		String nombreArchivo = switch (tipoMovimiento) {
			case "ACTA_INICIAL" -> "Acta_Inicial_" + numActa + ".pdf";
			case "TRASLADO"     -> "Acta_Traslado_" + numActa + ".pdf";
			case "BAJA"         -> "Acta_Baja_" + numActa + ".pdf";
			default             -> "Acta_Asignacion_" + numActa + ".pdf";
		};

		String fechaStr = (fechaMantenimiento != null)
				? fechaMantenimiento.format(DateTimeFormatter.ISO_DATE)
				: "-";

		String tipoStr = (tipoMantenimiento == null || tipoMantenimiento.isBlank())
				? "N/A"
				: tipoMantenimiento.trim();

		String detalleStr = (detalleMantenimiento == null || detalleMantenimiento.isBlank())
				? "-"
				: detalleMantenimiento.trim();

		String nombre = (nombreCliente == null || nombreCliente.isBlank()) ? "Custodio" : nombreCliente;

		try {
			MimeMessage mensaje = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

			helper.setFrom(remitente, nombreRemitente);
			helper.setTo(destinatario);
			helper.setSubject("Mantenimiento - " + etiqueta + " de Activos No. " + numActa);

			String cuerpo = """
					<div style="font-family: Arial, sans-serif; color: #333;">
					  <h2 style="color: #6a1b9a;">CRESIO - Gesti&oacute;n de Activos</h2>
					  <p>Estimado/a <strong>%s</strong>,</p>
					  <p>Se adjunta el <strong>%s N.&ordm; %s</strong> correspondiente
					     a los activos fijos bajo su responsabilidad.</p>
					  <p><strong>Detalle de mantenimiento:</strong></p>
					  <table style="border-collapse: collapse; width: 100%%; font-size: 14px;">
					    <tr>
					      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">Fecha mantenimiento</td>
					      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">%s</td>
					    </tr>
					    <tr>
					      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">Tipo mantenimiento</td>
					      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">%s</td>
					    </tr>
					  </table>
					  <p style="margin-top: 12px;"><strong>Detalle:</strong></p>
					  <div style="white-space: pre-wrap; border: 1px solid #e5e7eb; padding: 10px; border-radius: 4px;">
					    %s
					  </div>
					  <br/>
					  <p>Por favor revise el documento adjunto y conserve una copia para sus registros.</p>
					  <p style="font-size: 12px; color: #888;">
					    Este es un mensaje autom&aacute;tico del sistema de Gesti&oacute;n de Activos CRESIO.
					    No responda a este correo.
					  </p>
					</div>
					""".formatted(nombre, etiqueta, numActa, fechaStr, tipoStr, detalleStr);

			helper.setText(cuerpo, true);
			helper.addAttachment(nombreArchivo, new ByteArrayResource(pdfBytes), "application/pdf");

			if (imagenes != null) {
				for (MultipartFile img : imagenes) {
					if (img == null || img.isEmpty()) continue;
					String contentType = img.getContentType();
					if (contentType != null && !contentType.startsWith("image/")) {
						continue;
					}
					String originalName = (img.getOriginalFilename() == null || img.getOriginalFilename().isBlank())
							? "imagen_mantenimiento"
							: img.getOriginalFilename();
					helper.addAttachment(originalName, new ByteArrayResource(img.getBytes()),
							contentType != null ? contentType : "application/octet-stream");
				}
			}

			mailSender.send(mensaje);
			log.info("Correo con {} y mantenimiento enviado a {}", etiqueta, destinatario);

		} catch (Exception e) {
			log.error("Error al enviar correo con acta y mantenimiento a {}: {}", destinatario, e.getMessage(), e);
		}
	}

	/**
	 * Envia el informe de mantenimiento con PDF adjunto.
	 */
	public void enviarInformeMantenimientoConPdf(String destinatario, String nombreCliente,
			String numInforme, byte[] pdfBytes,
			LocalDate fechaMantenimiento, String tipoMantenimiento, String detalleMantenimiento) {

		if (destinatario == null || destinatario.isBlank()) {
			log.warn("No se envio correo: destinatario vacio");
			return;
		}

		String fechaStr = (fechaMantenimiento != null)
				? fechaMantenimiento.format(DateTimeFormatter.ISO_DATE)
				: "-";

		String tipoStr = (tipoMantenimiento == null || tipoMantenimiento.isBlank())
				? "N/A"
				: tipoMantenimiento.trim();

		String detalleStr = (detalleMantenimiento == null || detalleMantenimiento.isBlank())
				? "-"
				: detalleMantenimiento.trim();

		String nombre = (nombreCliente == null || nombreCliente.isBlank()) ? "Custodio" : nombreCliente;

		try {
			MimeMessage mensaje = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

			helper.setFrom(remitente, nombreRemitente);
			helper.setTo(destinatario);
			helper.setSubject("Mantenimiento - Informe No. " + numInforme);

			String cuerpo = """
					<div style="font-family: Arial, sans-serif; color: #333;">
					  <h2 style="color: #6a1b9a;">CRESIO - Gesti&oacute;n de Activos</h2>
					  <p>Estimado/a <strong>%s</strong>,</p>
					  <p>Se adjunta el <strong>Informe de Mantenimiento N.&ordm; %s</strong>.</p>
					  <p><strong>Detalle de mantenimiento:</strong></p>
					  <table style="border-collapse: collapse; width: 100%%; font-size: 14px;">
					    <tr>
					      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">Fecha mantenimiento</td>
					      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">%s</td>
					    </tr>
					    <tr>
					      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">Tipo mantenimiento</td>
					      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">%s</td>
					    </tr>
					  </table>
					  <p style="margin-top: 12px;"><strong>Observaci&oacute;n:</strong></p>
					  <div style="white-space: pre-wrap; border: 1px solid #e5e7eb; padding: 10px; border-radius: 4px;">
					    %s
					  </div>
					  <br/>
					  <p>Por favor revise el documento adjunto y conserve una copia para sus registros.</p>
					  <p style="font-size: 12px; color: #888;">
					    Este es un mensaje autom&aacute;tico del sistema de Gesti&oacute;n de Activos CRESIO.
					    No responda a este correo.
					  </p>
					</div>
					""".formatted(nombre, numInforme, fechaStr, tipoStr, detalleStr);

			helper.setText(cuerpo, true);
			String nombreArchivo = "Informe_Mantenimiento_" + numInforme + ".pdf";
			helper.addAttachment(nombreArchivo, new ByteArrayResource(pdfBytes), "application/pdf");

			mailSender.send(mensaje);
			log.info("Correo de mantenimiento enviado a {}", destinatario);

		} catch (Exception e) {
			log.error("Error al enviar correo de mantenimiento a {}: {}", destinatario, e.getMessage(), e);
		}
	}

	public boolean enviarInformeMantenimiento(String destinatario, String asunto, EquiposResponseDTO equipo,
			LocalDate fechaMantenimiento, String tipoMantenimiento, String detalleMantenimiento) {

		if (destinatario == null || destinatario.isBlank()) {
			log.warn("No se envio correo: destinatario vacio");
			return false;
		}

		String asuntoFinal = (asunto == null || asunto.isBlank())
				? "Informe de mantenimiento de equipo"
				: asunto.trim();

		String fechaStr = (fechaMantenimiento != null)
				? fechaMantenimiento.format(DateTimeFormatter.ISO_DATE)
				: "-";

		String tipoStr = (tipoMantenimiento == null || tipoMantenimiento.isBlank())
				? "N/A"
				: tipoMantenimiento.trim();

		String detalleStr = (detalleMantenimiento == null || detalleMantenimiento.isBlank())
				? "-"
				: detalleMantenimiento.trim();

		String equipoId = equipo != null ? String.valueOf(equipo.getIdEquipo()) : "-";
		String codigoSap = (equipo != null && equipo.getCodigoSap() != null) ? equipo.getCodigoSap() : "-";
		String modelo = (equipo != null && equipo.getModelo() != null) ? equipo.getModelo() : "-";
		String serial = (equipo != null && equipo.getSerial() != null) ? equipo.getSerial() : "-";
		String marca = (equipo != null && equipo.getFkMarca() != null && equipo.getFkMarca().getNombre() != null)
				? equipo.getFkMarca().getNombre()
				: "-";
		String categoria = (equipo != null && equipo.getFkCategoria() != null && equipo.getFkCategoria().getNombre() != null)
				? equipo.getFkCategoria().getNombre()
				: "-";
		String estadoEquipo = (equipo != null && equipo.getEstadoEquipo() != null) ? equipo.getEstadoEquipo() : "-";

		String cuerpo = """
				<div style="font-family: Arial, sans-serif; color: #333;">
				  <h2 style="color: #2f855a;">Informe de mantenimiento</h2>
				  <p>Se registra el siguiente mantenimiento de equipo informatico:</p>
				  <table style="border-collapse: collapse; width: 100%%; font-size: 14px;">
				    <tr>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">Equipo ID</td>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">%s</td>
				    </tr>
				    <tr>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">Codigo SAP</td>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">%s</td>
				    </tr>
				    <tr>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">Modelo</td>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">%s</td>
				    </tr>
				    <tr>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">Serial</td>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">%s</td>
				    </tr>
				    <tr>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">Marca</td>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">%s</td>
				    </tr>
				    <tr>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">Categoria</td>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">%s</td>
				    </tr>
				    <tr>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">Estado equipo</td>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">%s</td>
				    </tr>
				    <tr>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">Fecha mantenimiento</td>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">%s</td>
				    </tr>
				    <tr>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">Tipo mantenimiento</td>
				      <td style="padding: 6px 8px; border: 1px solid #e5e7eb;">%s</td>
				    </tr>
				  </table>
				  <p style="margin-top: 12px;"><strong>Detalle:</strong></p>
				  <div style="white-space: pre-wrap; border: 1px solid #e5e7eb; padding: 10px; border-radius: 4px;">
				    %s
				  </div>
				  <br/>
				  <p style="font-size: 12px; color: #888;">
				    Este es un mensaje automatico del sistema de Gestion de Activos.
				    No responda a este correo.
				  </p>
				</div>
				""".formatted(equipoId, codigoSap, modelo, serial, marca, categoria,
						estadoEquipo, fechaStr, tipoStr, detalleStr);

		try {
			MimeMessage mensaje = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

			helper.setFrom(remitente, nombreRemitente);
			helper.setTo(destinatario);
			helper.setSubject(asuntoFinal);
			helper.setText(cuerpo, true);

			mailSender.send(mensaje);
			log.info("Correo de mantenimiento enviado a {}", destinatario);
			return true;

		} catch (Exception e) {
			log.error("Error al enviar correo de mantenimiento a {}: {}", destinatario, e.getMessage(), e);
			return false;
		}
	}
}
