package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uisrael.gestionactivosapi.aplicacion.servicios.CorreoSchedulerService;
import com.uisrael.gestionactivosapi.aplicacion.servicios.MantenimientoProgramadoService;
import com.uisrael.gestionactivosapi.aplicacion.servicios.NotificacionService;

@Component
public class MantenimientoScheduler {

    private final MantenimientoProgramadoService programadoService;
    private final NotificacionService notificacionService;
    private final ObjectProvider<CorreoSchedulerService> correoSchedulerService;

    public MantenimientoScheduler(MantenimientoProgramadoService programadoService,
            NotificacionService notificacionService,
            ObjectProvider<CorreoSchedulerService> correoSchedulerService) {
        this.programadoService = programadoService;
        this.notificacionService = notificacionService;
        this.correoSchedulerService = correoSchedulerService;
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void verificarMantenimientosPendientes() {
        programadoService.obtenerPendientesParaNotificar().forEach(mp -> {
            notificacionService.crear(
                    mp.getTecnicoId(),
                    "Mantenimiento pendiente: " + (mp.getFkEquipo() != null ? mp.getFkEquipo().getCodigoSap() : mp.getEquipoId()),
                    "/mantenimiento/nuevo?equipoId=" + mp.getEquipoId(),
                    null);
            CorreoSchedulerService correo = correoSchedulerService.getIfAvailable();
            if (correo != null) {
                correo.enviarAvisoMantenimiento(mp);
            }
        });
    }
}
