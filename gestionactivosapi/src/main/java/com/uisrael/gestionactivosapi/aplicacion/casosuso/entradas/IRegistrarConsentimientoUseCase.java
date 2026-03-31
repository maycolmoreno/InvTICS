package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ConsentimientoMonitoreoJpa;

public interface IRegistrarConsentimientoUseCase {

    ConsentimientoMonitoreoJpa ejecutar(Integer tecnicoId, String versionTerminos, String ipAceptacion);
}
