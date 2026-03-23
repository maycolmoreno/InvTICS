package com.uisrael.gestionactivosapi.aplicacion.servicios;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class CorreoMantenimientoService {

    private static final Logger log = LoggerFactory.getLogger(CorreoMantenimientoService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String remitente;

    @Value("${app.mail.from-name:CRESIO - Gestion de Activos}")
    private String nombreRemitente;

    public CorreoMantenimientoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarInformeMantenimientoConPdf(String destinatario, String nombreCliente,
            String numInforme, byte[] pdfBytes,
            LocalDate fechaMantenimiento, String tipoMantenimiento, String detalleMantenimiento) {

        if (destinatario == null || destinatario.isBlank()) {
            log.warn("No se envio correo: destinatario vacio");
            return;
        }

        String fechaStr = fechaMantenimiento != null
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
                      <h2 style="color: #6a1b9a;">CRESIO - Gestion de Activos</h2>
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
                      <p style="margin-top: 12px;"><strong>Observacion:</strong></p>
                      <div style="white-space: pre-wrap; border: 1px solid #e5e7eb; padding: 10px; border-radius: 4px;">
                        %s
                      </div>
                      <br/>
                      <p>Por favor revise el documento adjunto y conserve una copia para sus registros.</p>
                      <p style="font-size: 12px; color: #888;">
                        Este es un mensaje automatico del sistema de Gestion de Activos CRESIO.
                        No responda a este correo.
                      </p>
                    </div>
                    """.formatted(nombre, numInforme, fechaStr, tipoStr, detalleStr);

            helper.setText(cuerpo, true);
            helper.addAttachment(
                    "Informe_Mantenimiento_" + numInforme + ".pdf",
                    new ByteArrayResource(pdfBytes),
                    "application/pdf");

            mailSender.send(mensaje);
            log.info("Correo de mantenimiento enviado a {}", destinatario);
        } catch (Exception e) {
            log.error("Error al enviar correo de mantenimiento a {}: {}", destinatario, e.getMessage(), e);
        }
    }
}
