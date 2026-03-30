package com.uisrael.gestionactivosapi.infraestructura.servicios;

import com.uisrael.gestionactivosapi.dominio.puertos.servicios.EnviadorCorreoPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientoProgramadoJpa;

public class CorreoSchedulerService {

    private final EnviadorCorreoPuerto enviadorCorreo;

    public CorreoSchedulerService(EnviadorCorreoPuerto enviadorCorreo) {
        this.enviadorCorreo = enviadorCorreo;
    }

    public void enviarAvisoMantenimiento(MantenimientoProgramadoJpa programado) {
        if (programado.getFkTecnicoAsignado() == null || programado.getFkTecnicoAsignado().getCorreo() == null) {
            return;
        }
        String destinatario = programado.getFkTecnicoAsignado().getCorreo();
        String asunto = "Mantenimiento pendiente";
        String contenido = "Equipo: "
                + (programado.getFkEquipo() != null ? programado.getFkEquipo().getCodigoSap() : programado.getEquipoId())
                + "\nAbrir: /mantenimiento/nuevo?equipoId=" + programado.getEquipoId();
        enviadorCorreo.enviarCorreo(destinatario, asunto, contenido);
    }
}
