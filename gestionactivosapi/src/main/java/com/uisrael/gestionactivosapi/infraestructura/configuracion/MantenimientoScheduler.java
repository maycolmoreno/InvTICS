package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uisrael.gestionactivosapi.infraestructura.servicios.CorreoSchedulerService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.MantenimientoProgramadoService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.NotificacionService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.PushNotificacionService;

@Component
public class MantenimientoScheduler {

    private final MantenimientoProgramadoService programadoService;
    private final NotificacionService notificacionService;
    private final ObjectProvider<CorreoSchedulerService> correoSchedulerService;
    private final PushNotificacionService pushNotificacionService;

    public MantenimientoScheduler(MantenimientoProgramadoService programadoService,
            NotificacionService notificacionService,
            ObjectProvider<CorreoSchedulerService> correoSchedulerService,
            PushNotificacionService pushNotificacionService) {
        this.programadoService = programadoService;
        this.notificacionService = notificacionService;
        this.correoSchedulerService = correoSchedulerService;
        this.pushNotificacionService = pushNotificacionService;
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void verificarMantenimientosPendientes() {
        programadoService.obtenerPendientesParaNotificar().forEach(mp -> {
            String mensaje = "Pendiente: " + (mp.getFkEquipo() != null ? mp.getFkEquipo().getCodigoSap() : mp.getEquipoId());
            String url = "/mantenimiento/nuevo?equipoId=" + mp.getEquipoId();

            notificacionService.crear(mp.getTecnicoId(), mensaje, url, null);

            // Email
            CorreoSchedulerService correo = correoSchedulerService.getIfAvailable();
            if (correo != null) {
                correo.enviarAvisoMantenimiento(mp);
            }

            // Push notification
            pushNotificacionService.enviar(mp.getTecnicoId(),
                    "Mantenimiento pendiente", mensaje, url);
        });
    }
}
