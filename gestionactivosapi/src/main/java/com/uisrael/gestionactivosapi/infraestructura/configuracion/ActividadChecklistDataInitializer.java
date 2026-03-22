package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadChecklistJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadChecklistJpaRepositorio;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActividadChecklistDataInitializer implements ApplicationRunner {

    private final IActividadChecklistJpaRepositorio actividadChecklistRepositorio;

    @Override
    public void run(ApplicationArguments args) {
        if (actividadChecklistRepositorio.count() > 0) {
            return;
        }

        actividadChecklistRepositorio.saveAll(List.of(
                actividad("Limpieza de polvo interior", "LIMPIEZA_FISICA", 1),
                actividad("Limpieza de teclado y pantalla", "LIMPIEZA_FISICA", 2),
                actividad("Revision de ventiladores y disipadores", "LIMPIEZA_FISICA", 3),
                actividad("Actualizacion de sistema operativo", "SOFTWARE", 1),
                actividad("Actualizacion de drivers", "SOFTWARE", 2),
                actividad("Escaneo de antivirus", "SOFTWARE", 3),
                actividad("Revision de programas instalados", "SOFTWARE", 4),
                actividad("Prueba de bateria o fuente de poder", "HARDWARE", 1),
                actividad("Revision de puertos USB y conexiones", "HARDWARE", 2),
                actividad("Prueba de memoria RAM y disco", "HARDWARE", 3)));
    }

    private ActividadChecklistJpa actividad(String nombre, String categoria, int orden) {
        ActividadChecklistJpa actividad = new ActividadChecklistJpa();
        actividad.setNombre(nombre);
        actividad.setCategoria(categoria);
        actividad.setOrden(orden);
        actividad.setEstado(Boolean.TRUE);
        return actividad;
    }
}
