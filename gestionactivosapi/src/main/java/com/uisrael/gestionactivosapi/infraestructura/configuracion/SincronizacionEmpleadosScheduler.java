package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uisrael.gestionactivosapi.aplicacion.servicios.SincronizacionEmpleadosService;

/**
 * Sincronizacion periodica de empleados. Solo actua si hay una fuente
 * configurada (EMPLEADOS_SYNC_URL o EMPLEADOS_SYNC_ARCHIVO); si no, no hace nada.
 */
@Component
public class SincronizacionEmpleadosScheduler {

    private static final Logger log = LoggerFactory.getLogger(SincronizacionEmpleadosScheduler.class);

    private final SincronizacionEmpleadosService sincronizacionService;

    public SincronizacionEmpleadosScheduler(SincronizacionEmpleadosService sincronizacionService) {
        this.sincronizacionService = sincronizacionService;
    }

    @Scheduled(cron = "${empleados.sync.cron:0 30 6 * * *}")
    public void sincronizarEmpleados() {
        if (!sincronizacionService.fuenteConfigurada()) {
            return;
        }
        try {
            sincronizacionService.sincronizarDesdeFuente("scheduler");
        } catch (Exception e) {
            log.warn("Sincronizacion programada de empleados fallo: {}", e.getMessage());
        }
    }
}
