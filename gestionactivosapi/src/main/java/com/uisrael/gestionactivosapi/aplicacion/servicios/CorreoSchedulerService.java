package com.uisrael.gestionactivosapi.aplicacion.servicios;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientoProgramadoJpa;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(JavaMailSender.class)
public class CorreoSchedulerService {

    private final JavaMailSender mailSender;

    public void enviarAvisoMantenimiento(MantenimientoProgramadoJpa programado) {
        if (programado.getFkTecnicoAsignado() == null || programado.getFkTecnicoAsignado().getCorreo() == null) {
            return;
        }
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(programado.getFkTecnicoAsignado().getCorreo());
        msg.setSubject("Mantenimiento pendiente");
        msg.setText("Equipo: "
                + (programado.getFkEquipo() != null ? programado.getFkEquipo().getCodigoSap() : programado.getEquipoId())
                + "\nAbrir: /mantenimiento/nuevo?equipoId=" + programado.getEquipoId());
        mailSender.send(msg);
    }
}
